package org.mri.repositories;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandlersRepository {

    private final Map<CtTypeReference, List<CtMethodImpl>> handlers;

    public EventHandlersRepository() {
        this.handlers = new HashMap<>();
    }

    public Map<CtTypeReference, List<CtMethodImpl>> findAll() {
        return handlers;
    }

    public void add(CtTypeReference eventType, CtMethodImpl handlerMethod) {
        List<CtMethodImpl> handlersOfEventType = handlers.get(eventType);
        if(handlersOfEventType == null) {
            handlersOfEventType = new ArrayList<>();
            handlers.put(eventType, handlersOfEventType);
        }
        handlersOfEventType.add(handlerMethod);
    }
}
