package org.mri.processors.axon3;

import org.mri.source.eventHandlers.EventHandlers;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EventHandlersProcessor extends AbstractProcessor<CtMethodImpl> {

    private static final String AXON_EVENT_HANDLER = "org.axonframework.eventhandling.EventHandler";
    private static final String AXON_EVENT_SOURCING_HANDLER = "org.axonframework.eventsourcing.EventSourcingHandler";
    private static final String AXON_SAGA_HANDLER = "org.axonframework.eventhandling.saga.SagaEventHandler";
    private static final List<String> EVENT_HANDLER_ANNOTATIONS = Arrays.asList(
        AXON_EVENT_HANDLER,
        AXON_EVENT_SOURCING_HANDLER,
        AXON_SAGA_HANDLER
    );
    private EventHandlers repository;

    public EventHandlersProcessor(EventHandlers repository) {
        super();
        this.repository = repository;
    }

    @Override
    public void process(CtMethodImpl method) {
        if (isEventHandler(method)) {
            CtTypeReference eventType = ((CtParameter) method.getParameters().get(0)).getType();
            repository.add(eventType, method);
        }
    }

    private boolean isEventHandler(CtMethodImpl method) {
        return method.getAnnotations().stream()
            .anyMatch(isEventHandlerAnnotation());
    }

    private Predicate<CtAnnotation> isEventHandlerAnnotation() {
        return annotation -> EVENT_HANDLER_ANNOTATIONS.contains(annotation.getActualAnnotation().annotationType().getName());
    }
}
