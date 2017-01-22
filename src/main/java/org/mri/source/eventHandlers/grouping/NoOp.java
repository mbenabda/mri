package org.mri.source.eventHandlers.grouping;

import spoon.reflect.reference.CtTypeReference;

public class NoOp implements EventHandlersGroupingStrategy {
    @Override
    public Object groupKey(CtTypeReference eventType) {
        return eventType;
    }
}
