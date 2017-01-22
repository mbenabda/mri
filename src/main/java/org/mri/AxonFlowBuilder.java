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
    private final EventHandlerIdentificationStrategy eventHandlerIdentificationStrategy;
    private final MethodCallHierarchyBuilder callHierarchyBuilder;
    private AggregatesRepository aggregatesRepository;
    private final CommandHandlersRepository commandHandlersRepository;

    public AxonFlowBuilder(MethodCallHierarchyBuilder callHierarchyBuilder,
                           EventHandlerIdentificationStrategy eventHandlers,
                           CommandHandlersRepository commandHandlersRepository,
                           AggregatesRepository aggregatesRepository) {
        this.eventHandlerIdentificationStrategy = eventHandlers;
        this.commandHandlersRepository = commandHandlersRepository;
        this.callHierarchyBuilder = callHierarchyBuilder;
        this.aggregatesRepository = aggregatesRepository;
    }

    List<AxonNode> buildFlow(ArrayList<CtExecutableReference> methodReferences) {
        List<AxonNode> nodes = new ArrayList<>();
        for (CtExecutableReference each : methodReferences) {
            AxonNode root = new AxonNode(AxonNode.Type.CONTROLLER, each);
            nodes.add(root);
            buildCommandFlow(root);
        }
        return nodes;
    }

    private void buildCommandFlow(AxonNode node) {
        for (MethodCall call : this.callHierarchyBuilder.matchingCallsInBlock(node.reference(), calleeIs(aCommand()))) {
            CtExecutableReference callee = call.reference();
            CtTypeReference command = callee.getDeclaringType();

            AxonNode commandConstructionNode = new AxonNode(AxonNode.Type.COMMAND, callee);
            node.add(commandConstructionNode);
            AxonNode commandHandlerNode = new AxonNode(
                AxonNode.Type.COMMAND_HANDLER,
                commandHandlersRepository
                    .handlerOfCommand(command)
                    .getReference()
            );
            commandConstructionNode.add(commandHandlerNode);
            buildAggregateFlow(commandHandlerNode);
        }
    }

    private void buildAggregateFlow(AxonNode node) {
        for (MethodCall call : this.callHierarchyBuilder.matchingCallsInBlock(node.reference(), calleeIs(anAggregate()))) {
            AxonNode aggregateNode = new AxonNode(AxonNode.Type.AGGREGATE, call.reference());
            buildEventFlow(aggregateNode);
            if (aggregateNode.hasChildren()) {
                node.add(aggregateNode);
            }
        }
    }

    private AxonNode buildEventFlow(AxonNode node) {
        for (MethodCall eventConstruction : this.callHierarchyBuilder.matchingCallsInBlock(node.reference(), calleeIs(anEvent()))) {
            AxonNode eventNode = new AxonNode(AxonNode.Type.EVENT, eventConstruction.reference());
            node.add(eventNode);
            for (CtMethodImpl eventHandler : eventHandlerIdentificationStrategy.findEventHandlers(eventNode.reference().getDeclaringType())) {
                AxonNode eventHandlerNode = new AxonNode(AxonNode.Type.EVENT_LISTENER, eventHandler.getReference());
                eventNode.add(eventHandlerNode);
                buildCommandFlow(eventHandlerNode);
            }
        }
        return node;
    }

    private Predicate<CtTypeReference> anEvent() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference callee) {
                return eventHandlerIdentificationStrategy.isAnEvent(callee);
            }
        };
    }

    private Predicate<MethodCall> calleeIs(final Predicate<CtTypeReference> calleeSpecification) {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall input) {
                CtTypeReference callee = input.reference().getDeclaringType();
                return calleeSpecification.apply(callee);
            }
        };
    }

    private Predicate<CtTypeReference> aCommand() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference callee) {
                return commandHandlersRepository.isCommand(callee);
            }
        };
    }

    private Predicate<CtTypeReference> anAggregate() {
        return new Predicate<CtTypeReference>() {
            @Override
            public boolean apply(CtTypeReference callee) {
                return aggregatesRepository.isAggregate(callee);
            }
        };
    }
}
