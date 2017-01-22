package org.mri.source;

import org.mri.ASTHelpers;
import org.mri.MethodCall;
import org.mri.MethodWrapper;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class MethodCallsHierarchy {
    private final Map<MethodWrapper, List<CtExecutableReference>> callList;
    private final ClassesHierarchy classesHierarchy;

    public MethodCallsHierarchy(MethodsExecutions methodsExecutions, ClassesHierarchy classesHierarchy) {
        this.callList = methodsExecutions.findAll();
        this.classesHierarchy = classesHierarchy;
    }

    public Collection<CtExecutableReference> referencesOfMethod(String methodName) {
        return findExecutablesForMethodName(methodName);
    }

    public Iterable<MethodCall> callsInBlock(CtExecutableReference block, Predicate<MethodCall> predicate) {
        MethodCall call = buildCalleesMethodHierarchy(block);
        return call.asList().stream()
            .filter(predicate)
            .collect(toList());
    }

    private List<CtExecutableReference> findExecutablesForMethodName(String methodName) {
        return callList.keySet().stream()
            .map(methodWrapper -> methodWrapper.method().getReference())
            .filter(executableReference -> {
                String executableReferenceMethodName = ASTHelpers.signatureOf(executableReference);
                return (executableReferenceMethodName.equals(methodName)
                    || executableReference.toString().contains(methodName)
                    || executableReference.toString().matches(methodName));
            })
            .collect(toList());
    }

    private MethodCall buildCalleesMethodHierarchy(CtExecutableReference executableReference) {
        MethodCall methodCall = new MethodCall(executableReference);
        buildCallHierarchy(executableReference, new HashSet<>(), methodCall);
        return methodCall;
    }

    private void buildCallHierarchy(
        CtExecutableReference executableReference, Set<CtExecutableReference> alreadyVisited, MethodCall methodCall) {
        if (alreadyVisited.contains(executableReference)) {
            return;
        }
        alreadyVisited.add(executableReference);
        List<CtExecutableReference> callListForMethod = callList.get(new MethodWrapper(executableReference));
        if (callListForMethod == null) {
            return;
        }
        for (CtExecutableReference eachReference : callListForMethod) {
            MethodCall childCall = new MethodCall(eachReference);
            methodCall.add(childCall);

            buildCallHierarchy(eachReference, alreadyVisited, childCall);
            Set<CtTypeReference> subclasses = classesHierarchy.subclassesOf(eachReference.getDeclaringType());
            for (CtTypeReference subclass : subclasses) {
                CtExecutableReference reference = eachReference.getOverridingExecutable(subclass);
                if (reference != null) {
                    childCall = new MethodCall(reference);
                    methodCall.add(childCall);
                    buildCallHierarchy(reference, alreadyVisited, childCall);
                }
            }
        }
    }
}
