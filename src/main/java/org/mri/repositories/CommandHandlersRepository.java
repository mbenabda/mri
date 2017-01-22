package org.mri.repositories;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlersRepository {

    private final Map<CtTypeReference, CtMethodImpl> handlerPerCommandType;

    public CommandHandlersRepository() {
        this.handlerPerCommandType = new HashMap<>();
    }

    public Map<CtTypeReference, CtMethodImpl> findAll() {
        return handlerPerCommandType;
    }

    public void add(CtTypeReference commandType, CtMethodImpl handler) {
        handlerPerCommandType.put(commandType, handler);
    }
}
