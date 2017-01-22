package org.mri.source.eventHandlers.grouping;

import spoon.reflect.reference.CtTypeReference;

public interface EventHandlersGroupingStrategy {
    Object groupKey(CtTypeReference eventType);
}
