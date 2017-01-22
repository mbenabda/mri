package org.mri.repositories;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlersRepository {

    private final Map<CtTypeReference, CtMethodImpl> handlers;

    public CommandHandlersRepository() {
        this.handlers = new HashMap<>();
    }

    public Map<CtTypeReference, CtMethodImpl> findAll() {
        return handlers;
    }

    public void add(CtTypeReference commandType, CtMethodImpl handler) {
        handlers.put(commandType, handler);
    }
}
