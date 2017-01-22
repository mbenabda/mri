package org.mri.processors.axon2;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.mri.repositories.CommandHandlersRepository;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.support.reflect.declaration.CtMethodImpl;

public class CommandHandlersProcessor extends AbstractProcessor<CtMethodImpl> {
    private static final String AXON_COMMAND_HANDLER = "org.axonframework.commandhandling.annotation.CommandHandler";

    private CommandHandlersRepository repository;

    public CommandHandlersProcessor(CommandHandlersRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public void process(CtMethodImpl method) {
        if (isCommandHandler(method)) {
            repository.add(((CtParameter)method.getParameters().get(0)).getType(), method);
        }
    }

    private boolean isCommandHandler(CtMethodImpl method) {
        return Iterables.tryFind(
            method.getAnnotations(),
            isCommandHandlerAnnotation()
        ).isPresent();
    }

    private Predicate<CtAnnotation> isCommandHandlerAnnotation() {
        return new Predicate<CtAnnotation>() {
            @Override
            public boolean apply(CtAnnotation annotation) {
                return AXON_COMMAND_HANDLER.equals(annotation.getActualAnnotation().annotationType().getName());
            }
        };
    }
}
