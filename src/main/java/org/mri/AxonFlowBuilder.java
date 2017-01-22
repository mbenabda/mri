package org.mri;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());
        for (MethodCall call : callsWhereCalleeIsACommand(methodCall.asList())) {
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

    private Iterable<MethodCall> callsWhereCalleeIsACommand(List<MethodCall> calls) {
        return Iterables.filter(calls, calleeIsACommand());
    }

    private Predicate<MethodCall> calleeIsACommand() {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall input) {
                CtTypeReference callee = input.reference().getDeclaringType();
                return commandHandlersRepository.isCommand(callee);
            }
        };
    }

    private void buildAggregateFlow(AxonNode node) {
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());

        for (MethodCall aggregateCall : callsWhereCalleeIsAnAggregate(methodCall.asList())) {
            AxonNode aggregateNode = new AxonNode(AxonNode.Type.AGGREGATE, aggregateCall.reference());
            buildEventFlow(aggregateNode);
            if (aggregateNode.hasChildren()) {
                node.add(aggregateNode);
            }
        }
    }

    private Iterable<MethodCall> callsWhereCalleeIsAnAggregate(List<MethodCall> calls) {
        return Iterables.filter(calls, calleeIsAnAggregate());
    }

    private Predicate<? super MethodCall> calleeIsAnAggregate() {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall input) {
                CtTypeReference callee = input.reference().getDeclaringType();
                return aggregatesRepository.isAggregate(callee);
            }
        };
    }

    private AxonNode buildEventFlow(AxonNode node) {
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());

        Iterable<MethodCall> eventConstructionInstances =
                Iterables.filter(methodCall.asList(), eventHandlerIdentificationStrategy.isEventPredicate());
        for (MethodCall eventConstruction : eventConstructionInstances) {
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
}
