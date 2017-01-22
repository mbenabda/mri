package org.mri;

import org.mri.repositories.ClassHierarchyRepository;
import org.mri.repositories.MethodExecutionRepository;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class MethodCallsHierarchyBuilder {
    private final Map<MethodWrapper, List<CtExecutableReference>> callList;
    private final Map<CtTypeReference, Set<CtTypeReference>> classHierarchy;

    public MethodCallsHierarchyBuilder(MethodExecutionRepository methodExecutionRepository, ClassHierarchyRepository classHierarchyRepository) {
        this.callList = methodExecutionRepository.findAll();
        this.classHierarchy = classHierarchyRepository.findAll();
    }

    public Collection<CtExecutableReference> referencesOfMethod(String methodName) {
        return findExecutablesForMethodName(methodName, callList);
    }

    public Iterable<MethodCall> callsInBlock(CtExecutableReference block, Predicate<MethodCall> predicate) {
        MethodCall call = buildCalleesMethodHierarchy(block);
        return call.asList().stream()
            .filter(predicate)
            .collect(toSet());
    }

    private static List<CtExecutableReference> findExecutablesForMethodName(String methodName, Map<MethodWrapper, List<CtExecutableReference>> callList) {
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
            Set<CtTypeReference> subclasses = classHierarchy.get(eachReference.getDeclaringType());
            if (subclasses != null) {
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
}
