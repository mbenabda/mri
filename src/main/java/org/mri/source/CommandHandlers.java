package org.mri.source;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlers {

    private final Map<CtTypeReference, CtExecutableReference> handlerPerCommandType;

    public CommandHandlers() {
        this.handlerPerCommandType = new HashMap<>();
    }

    public void add(CtTypeReference commandType, CtMethodImpl handler) {
        handlerPerCommandType.put(commandType, handler.getReference());
    }

    public CtExecutableReference handlerOfCommand(CtTypeReference commandType) {
        return handlerPerCommandType.get(commandType);
    }

    public boolean isACommand(CtTypeReference candidate) {
        return handlerOfCommand(candidate) != null;
    }
}
