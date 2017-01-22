package org.mri.repositories.eventHandlers;

import spoon.reflect.reference.CtTypeReference;

public class NoEventHandlersGroupingStrategy implements EventHandlersGroupingStrategy {
    @Override
    public Object groupKey(CtTypeReference eventType) {
        return eventType;
    }
}
