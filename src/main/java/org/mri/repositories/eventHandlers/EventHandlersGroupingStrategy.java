package org.mri.repositories.eventHandlers;

import spoon.reflect.reference.CtTypeReference;

public interface EventHandlersGroupingStrategy {
    Object groupKey(CtTypeReference eventType);
}
