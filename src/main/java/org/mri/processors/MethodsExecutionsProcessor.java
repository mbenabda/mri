package org.mri.processors;

import org.mri.source.MethodsExecutions;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.reflect.declaration.CtExecutableImpl;

import java.util.List;

public class MethodsExecutionsProcessor extends AbstractProcessor<CtExecutableImpl> {
    private MethodsExecutions repository;

    public MethodsExecutionsProcessor(MethodsExecutions repository) {
        super();
        this.repository = repository;
    }

    @Override
    public void process(CtExecutableImpl ctMethod) {
        List<CtElement> elements = ctMethod.getElements(new AbstractFilter<CtElement>(CtElement.class) {
            @Override
            public boolean matches(CtElement ctElement) {
                return ctElement instanceof CtAbstractInvocation;
            }
        });
        for (CtElement element : elements) {
            CtAbstractInvocation invocation = (CtAbstractInvocation) element;
            repository.add(ctMethod, invocation.getExecutable());
        }
    }
}
