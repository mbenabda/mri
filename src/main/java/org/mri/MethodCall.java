package org.mri;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class MethodCall {
    private final CtExecutableReference reference;
    private List<MethodCall> calls = new ArrayList<>();

    public MethodCall(CtExecutableReference reference) {
        this.reference = reference;
    }

    public void add(MethodCall methodCall) {
        calls.add(methodCall);
    }

    public List<MethodCall> asList() {
        ArrayList<MethodCall> result = new ArrayList<>();
        collectRecursively(result);
        return result;
    }

    private void collectRecursively(List<MethodCall> accumulator) {
        accumulator.add(this);
        for (MethodCall call : calls) {
            call.collectRecursively(accumulator);
        }
    }

    public CtExecutableReference reference() {
        return reference;
    }

    public CtTypeReference getDeclaringType() {
        return reference.getDeclaringType();
    }
}
