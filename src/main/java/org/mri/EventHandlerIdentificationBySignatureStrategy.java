package org.mri;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.List;
import java.util.Map;

public class EventHandlerIdentificationBySignatureStrategy implements EventHandlerIdentificationStrategy {

    private final Map<CtTypeReference, List<CtMethodImpl>> eventHandlers;

    public EventHandlerIdentificationBySignatureStrategy(Map<CtTypeReference, List<CtMethodImpl>> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public List<CtMethodImpl> findEventHandlers(CtTypeReference type) {
        return this.eventHandlers.get(type);
    }

    @Override
    public boolean isAnEvent(CtTypeReference candidate) {
        return eventHandlers.containsKey(candidate);
    }
}
