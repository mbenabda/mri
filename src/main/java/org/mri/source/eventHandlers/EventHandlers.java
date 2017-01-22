package org.mri.source.eventHandlers;

import org.mri.source.eventHandlers.grouping.EventHandlersGroupingStrategy;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandlers {

    private final Map<Object, List<CtMethodImpl>> handlersPerEventType;
    private EventHandlersGroupingStrategy groupingStrategy;

    public EventHandlers(EventHandlersGroupingStrategy groupingStrategy) {
        this.handlersPerEventType = new HashMap<>();
        this.groupingStrategy = groupingStrategy;
    }

    public void add(CtTypeReference eventType, CtMethodImpl handlerMethod) {
        List<CtMethodImpl> handlersOfEventType = handlersPerEventType.get(groupKey(eventType));
        if(handlersOfEventType == null) {
            handlersOfEventType = new ArrayList<>();
            handlersPerEventType.put(groupKey(eventType), handlersOfEventType);
        }
        handlersOfEventType.add(handlerMethod);
    }

    public List<CtMethodImpl> findEventHandlers(CtTypeReference type) {
        return handlersPerEventType.get(groupKey(type));
    }

    public boolean isAnEvent(CtTypeReference candidate) {
        return handlersPerEventType.containsKey(groupKey(candidate));
    }

    private Object groupKey(CtTypeReference eventType) {
        return groupingStrategy.groupKey(eventType);
    }
}
