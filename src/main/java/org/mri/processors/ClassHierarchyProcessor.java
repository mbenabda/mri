package org.mri.processors;

import org.mri.source.ClassesHierarchy;
import spoon.processing.AbstractProcessor;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtClassImpl;

public class ClassHierarchyProcessor extends AbstractProcessor<CtClassImpl> {
    private ClassesHierarchy repository;

    public ClassHierarchyProcessor(ClassesHierarchy repository) {
        super();
        this.repository = repository;
    }

    @Override
    public void process(CtClassImpl clazz) {
        if (clazz.getReference().isAnonymous()) {
            return;
        }
        if (clazz.getSuperclass() != null) {
            repository.add(clazz.getReference(), clazz.getSuperclass());
        }
        for (Object o : clazz.getSuperInterfaces()) {
            CtTypeReference superclass = (CtTypeReference) o;
            repository.add(clazz.getReference(), superclass);
        }
    }
}