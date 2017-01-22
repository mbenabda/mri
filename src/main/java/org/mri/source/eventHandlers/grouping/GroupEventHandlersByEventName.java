package org.mri.source.eventHandlers.grouping;

import spoon.reflect.reference.CtTypeReference;

public class GroupEventHandlersByEventName implements EventHandlersGroupingStrategy {
    @Override
    public Object groupKey(CtTypeReference eventType) {
        return eventType.getSimpleName();
    }
}
