package org.mri.processors.axon3;

import org.mri.repositories.CommandHandlersRepository;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.function.Predicate;

public class CommandHandlersProcessor extends AbstractProcessor<CtMethodImpl> {
    private static final String AXON_COMMAND_HANDLER = "org.axonframework.commandhandling.CommandHandler";

    private CommandHandlersRepository repository;

    public CommandHandlersProcessor(CommandHandlersRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public void process(CtMethodImpl method) {
        if (isCommandHandler(method)) {
            repository.add(((CtParameter) method.getParameters().get(0)).getType(), method);
        }
    }

    private boolean isCommandHandler(CtMethodImpl method) {
        return method.getAnnotations().stream()
            .anyMatch(isCommandHandlerAnnotation());
    }

    private Predicate<CtAnnotation> isCommandHandlerAnnotation() {
        return annotation -> AXON_COMMAND_HANDLER.equals(annotation.getActualAnnotation().annotationType().getName());
    }
}
