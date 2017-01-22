package org.mri.source;

import spoon.reflect.reference.CtTypeReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

public class ClassesHierarchy {
    private final Map<CtTypeReference, Set<CtTypeReference>> implementors;

    public ClassesHierarchy() {
        this.implementors = new HashMap<>();
    }

    public void add(CtTypeReference clazz, CtTypeReference superClass) {
        Set<CtTypeReference> subclasses = implementors.get(superClass);
        if (subclasses == null) {
            subclasses = new HashSet<>();
            implementors.put(superClass, subclasses);
        }
        subclasses.add(clazz);
    }

    public Set<CtTypeReference> subclassesOf(CtTypeReference type) {
        return implementors.getOrDefault(type, emptySet());
    }
}
