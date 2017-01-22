package org.mri;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandlerIdentificationByNameStrategy implements EventHandlerIdentificationStrategy {

    private final HashMap<String, List<CtMethodImpl>> eventHandlerByNames = new HashMap<>();

    public EventHandlerIdentificationByNameStrategy(Map<CtTypeReference, List<CtMethodImpl>> eventHandlers) {
        for (Map.Entry<CtTypeReference, List<CtMethodImpl>> entry : eventHandlers.entrySet()) {
            List<CtMethodImpl> collectedEventHandlers = eventHandlerByNames.get(entry.getKey().getSimpleName());
            if (collectedEventHandlers == null) {
                collectedEventHandlers = new ArrayList<>();
                eventHandlerByNames.put(entry.getKey().getSimpleName(), collectedEventHandlers);
            }
            collectedEventHandlers.addAll(entry.getValue());
        }
    }

    @Override
    public List<CtMethodImpl> findEventHandlers(CtTypeReference type) {
        return eventHandlerByNames.get(type.getSimpleName());
    }

    @Override
    public boolean isAnEvent(CtTypeReference candidate) {
        return eventHandlerByNames.containsKey(candidate.getSimpleName());
    }
}
