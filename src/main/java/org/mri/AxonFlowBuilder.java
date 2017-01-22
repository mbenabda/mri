package org.mri;

import com.google.common.base.Predicate;
import org.mri.repositories.AggregatesRepository;
import org.mri.repositories.CommandHandlersRepository;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.List;

public class AxonFlowBuilder {
    private final EventHandlerIdentificationStrategy eventHandlers;
    private final MethodCallsHierarchyBuilder methodCallsHierarchy;
    private AggregatesRepository aggregates;
    private final CommandHandlersRepository commandHandlers;

    public AxonFlowBuilder(MethodCallsHierarchyBuilder methodCallsHierarchy,
                           EventHandlerIdentificationStrategy eventHandlers,
                           CommandHandlersRepository commandHandlers,
                           AggregatesRepository aggregates) {
        this.eventHandlers = eventHandlers;
        this.commandHandlers = commandHandlers;
        this.methodCallsHierarchy = methodCallsHierarchy;
        this.aggregates = aggregates;
    }

    List<AxonNode> buildFlow(String methodName) throws MethodNotFoundException {
        ArrayList<CtExecutableReference> methodReferences = methodCallsHierarchy.referencesOfMethod(methodName);
        if (methodReferences.isEmpty()) {
            throw new MethodNotFoundException(methodName);
        }

        List<AxonNode> nodes = new ArrayList<>();
        for (CtExecutableReference each : methodReferences) {
            AxonNode root = new AxonNode(AxonNode.Type.CONTROLLER, each);
            nodes.add(root);
            buildCommandFlow(root);
        }
        return nodes;
    }

    private void buildCommandFlow(AxonNode node) {
        for (MethodCall call : this.methodCallsHierarchy.callsInBlock(node.reference(), declaredIn(aCommand()))) {
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
        for (MethodCall call : this.methodCallsHierarchy.callsInBlock(node.reference(), declaredIn(anAggregate()))) {
            AxonNode aggregateNode = new AxonNode(AxonNode.Type.AGGREGATE, call.reference());
            buildEventFlow(aggregateNode);
            if (aggregateNode.hasChildren()) {
                node.add(aggregateNode);
            }
        }
    }

    private AxonNode buildEventFlow(AxonNode node) {
        for (MethodCall eventConstruction : this.methodCallsHierarchy.callsInBlock(node.reference(), declaredIn(anEvent()))) {
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

    private Predicate<MethodCall> declaredIn(final Predicate<CtTypeReference> declaringTypeSpecification) {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall call) {
                return declaringTypeSpecification.apply(call.getDeclaringType());
            }
        };
    }

    private Predicate<CtTypeReference> aCommand() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference type) {
                return commandHandlers.isCommand(type);
            }
        };
    }

    private Predicate<CtTypeReference> anAggregate() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference type) {
                return aggregates.isAggregate(type);
            }
        };
    }

    private Predicate<CtTypeReference> anEvent() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference type) {
                return eventHandlers.isAnEvent(type);
            }
        };
    }
}
