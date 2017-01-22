package org.mri.source;

import org.mri.MethodWrapper;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.reflect.declaration.CtExecutableImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodsExecutions {
    private Map<MethodWrapper, List<CtExecutableReference>> callsByCaller;

    public MethodsExecutions() {
        this.callsByCaller = new HashMap<>();
    }

    public Map<MethodWrapper, List<CtExecutableReference>> findAll() {
        return callsByCaller;
    }

    public void add(CtExecutableImpl caller_, CtExecutableReference callee) {
        MethodWrapper caller = new MethodWrapper(caller_);

        List<CtExecutableReference> calls = callsByCaller.get(caller);
        if(calls == null) {
            calls = new ArrayList<>();
            callsByCaller.put(caller, calls);
        }
        calls.add(callee);
    }
}

