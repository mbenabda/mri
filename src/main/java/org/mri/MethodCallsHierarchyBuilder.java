package org.mri;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.mri.repositories.ClassHierarchyRepository;
import org.mri.repositories.MethodExecutionRepository;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

public class MethodCallsHierarchyBuilder {
    private final Map<MethodWrapper, List<CtExecutableReference>> callList;
    private final Map<CtTypeReference, Set<CtTypeReference>> classHierarchy;

    public MethodCallsHierarchyBuilder(MethodExecutionRepository methodExecutionRepository, ClassHierarchyRepository classHierarchyRepository) {
        this.callList = methodExecutionRepository.findAll();
        this.classHierarchy = classHierarchyRepository.findAll();
    }

    public ArrayList<CtExecutableReference> referencesOfMethod(String methodName) {
        ArrayList<CtExecutableReference > result = new ArrayList<>();
        for (CtExecutableReference executableReference : findExecutablesForMethodName(methodName, callList)) {
            result.add(executableReference);
        }
        return result;
    }

    public Iterable<MethodCall> callsInBlock(CtExecutableReference block, Predicate<MethodCall> predicate) {
        MethodCall call = buildCalleesMethodHierarchy(block);
        return Iterables.filter(call.asList(), predicate);
    }

    private static List<CtExecutableReference> findExecutablesForMethodName(String methodName, Map<MethodWrapper, List<CtExecutableReference>> callList) {
        ArrayList<CtExecutableReference> result = new ArrayList<>();
        for (MethodWrapper methodWrapper : callList.keySet()) {
            CtExecutableReference executableReference = methodWrapper.method().getReference();
            String executableReferenceMethodName = ASTHelpers.signatureOf(executableReference);
            if (executableReferenceMethodName.equals(methodName)
                    || executableReference.toString().contains(methodName)
                    || executableReference.toString().matches(methodName)) {
                result.add(executableReference);
            }
        }
        return result;
    }

    private MethodCall buildCalleesMethodHierarchy(CtExecutableReference executableReference) {
        MethodCall methodCall = new MethodCall(executableReference);
        buildCallHierarchy(executableReference, new HashSet<CtExecutableReference>(), methodCall);
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
