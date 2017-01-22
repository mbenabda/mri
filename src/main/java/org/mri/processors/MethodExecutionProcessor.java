package org.mri.processors;

import org.mri.repositories.MethodExecutionRepository;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.reflect.declaration.CtExecutableImpl;

import java.util.List;

public class MethodExecutionProcessor  extends AbstractProcessor<CtExecutableImpl> {
    private MethodExecutionRepository repository;

    public MethodExecutionProcessor(MethodExecutionRepository repository) {
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
