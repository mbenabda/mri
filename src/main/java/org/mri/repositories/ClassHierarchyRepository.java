package org.mri.repositories;

import spoon.reflect.reference.CtTypeReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassHierarchyRepository {
    private final Map<CtTypeReference, Set<CtTypeReference>> implementors;

    public ClassHierarchyRepository() {
        this.implementors = new HashMap<>();
    }

    public Map<CtTypeReference, Set<CtTypeReference>> findAll() {
        return implementors;
    }

    public void add(CtTypeReference clazz, CtTypeReference superClass) {
        Set<CtTypeReference> subclasses = implementors.get(superClass);
        if (subclasses == null) {
            subclasses = new HashSet<>();
            implementors.put(superClass, subclasses);
        }
        subclasses.add(clazz);
    }
}
