package org.mri;

import org.mri.repositories.AggregatesRepository;
import org.mri.repositories.CommandHandlersRepository;
import org.mri.repositories.eventHandlers.EventHandlersRepository;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class AxonFlowBuilder {
    private final EventHandlersRepository eventHandlers;
    private final MethodCallsHierarchyBuilder methodCallsHierarchy;
    private AggregatesRepository aggregates;
    private final CommandHandlersRepository commandHandlers;

    public AxonFlowBuilder(MethodCallsHierarchyBuilder methodCallsHierarchy,
                           EventHandlersRepository eventHandlers,
                           CommandHandlersRepository commandHandlers,
                           AggregatesRepository aggregates) {
        this.eventHandlers = eventHandlers;
        this.commandHandlers = commandHandlers;
        this.methodCallsHierarchy = methodCallsHierarchy;
        this.aggregates = aggregates;
    }

    List<AxonNode> buildFlow(String methodName) throws MethodNotFoundException {
        Collection<CtExecutableReference> methodReferences = methodCallsHierarchy.referencesOfMethod(methodName);
        if (methodReferences.isEmpty()) {
            throw new MethodNotFoundException(methodName);
        }

        List<AxonNode> nodes = new ArrayList<>();
        for (CtExecutableReference reference : methodReferences) {
            AxonNode node = new AxonNode(AxonNode.Type.CONTROLLER, reference);
            nodes.add(node);
            buildCommandFlow(node);
        }
        return nodes;
    }

    private void buildCommandFlow(AxonNode node) {
        for (MethodCall call : this.methodCallsHierarchy.callsInBlock(node.reference(), ofMethodsDeclaredIn(aCommand()))) {
            CtExecutableReference callReference = call.reference();
            CtTypeReference command = call.getDeclaringType();

            AxonNode commandConstructionNode = new AxonNode(AxonNode.Type.COMMAND, callReference);
            node.add(commandConstructionNode);
            AxonNode commandHandlerNode = new AxonNode(
                AxonNode.Type.COMMAND_HANDLER,
                commandHandlers
                    .handlerOfCommand(command)
                    .getReference()
            );
            commandConstructionNode.add(commandHandlerNode);
            buildAggregateFlow(commandHandlerNode);
        }
    }

    private void buildAggregateFlow(AxonNode node) {
        for (MethodCall call : this.methodCallsHierarchy.callsInBlock(node.reference(), ofMethodsDeclaredIn(anAggregate()))) {
            AxonNode aggregateNode = new AxonNode(AxonNode.Type.AGGREGATE, call.reference());
            buildEventFlow(aggregateNode);
            if (aggregateNode.hasChildren()) {
                node.add(aggregateNode);
            }
        }
    }

    private AxonNode buildEventFlow(AxonNode node) {
        for (MethodCall eventConstruction : this.methodCallsHierarchy.callsInBlock(node.reference(), ofMethodsDeclaredIn(anEvent()))) {
            AxonNode eventNode = new AxonNode(AxonNode.Type.EVENT, eventConstruction.reference());
            node.add(eventNode);
            for (CtMethodImpl eventHandler : eventHandlers.findEventHandlers(eventNode.reference().getDeclaringType())) {
                AxonNode eventHandlerNode = new AxonNode(AxonNode.Type.EVENT_LISTENER, eventHandler.getReference());
                eventNode.add(eventHandlerNode);
                buildCommandFlow(eventHandlerNode);
            }
        }
        return node;
    }

    private Predicate<MethodCall> ofMethodsDeclaredIn(final Predicate<CtTypeReference> declaringTypeSpecification) {
        return call -> declaringTypeSpecification.test(call.getDeclaringType());
    }

    private Predicate<CtTypeReference> aCommand() {
        return commandHandlers::isACommand;
    }

    private Predicate<CtTypeReference> anAggregate() { return aggregates::isAnAggregate; }

    private Predicate<CtTypeReference> anEvent() {
        return eventHandlers::isAnEvent;
    }
}
