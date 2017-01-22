package org.mri.processors;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.mri.repositories.EventHandlersRepository;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.Arrays;
import java.util.List;

public class EventHandlersProcessor extends AbstractProcessor<CtMethodImpl> {
    private static final String AXON_EVENT_HANDLER = "@org.axonframework.eventhandling.annotation.EventHandler";
    private static final String AXON_EVENT_SOURCING_HANDLER = "@org.axonframework.eventsourcing.annotation.EventSourcingHandler";
    private static final String AXON_SAGA_HANDLER = "@org.axonframework.saga.annotation.SagaEventHandler";
    private static final List<String> EVENT_HANDLER_ANNOTATIONS = Arrays.asList(
        AXON_EVENT_HANDLER,
        AXON_EVENT_SOURCING_HANDLER,
        AXON_SAGA_HANDLER
    );
    private EventHandlersRepository repository;

    public EventHandlersProcessor(EventHandlersRepository repository) {
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
        return Iterables.tryFind(
            method.getAnnotations(),
            isEventHandlerAnnotation()
        ).isPresent();
    }

    private Predicate<CtAnnotation> isEventHandlerAnnotation() {
        return new Predicate<CtAnnotation>() {
            @Override
            public boolean apply(CtAnnotation annotation) {
                return EVENT_HANDLER_ANNOTATIONS.contains(annotation.getSignature());
            }
        };
    }
}
