package org.mri.processors.axon3;

import org.mri.source.Aggregates;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.support.reflect.declaration.CtFieldImpl;

import java.util.function.Predicate;

public class AggregatesProcessor extends AbstractProcessor<CtFieldImpl> {

    private static final String AXON_AGGREGATE_IDENTIFIER_ANNOTATION = "org.axonframework.commandhandling.model.AggregateIdentifier";
    private Aggregates repository;

    public AggregatesProcessor(Aggregates repository) {
        this.repository = repository;
    }

    @Override
    public void process(CtFieldImpl field) {
        if (isAggregateIdentifier(field)) {
            repository.add(field.getDeclaringType().getReference());
        }
    }

    private boolean isAggregateIdentifier(CtFieldImpl field) {
        return field.getAnnotations().stream()
            .anyMatch(isAggregateIdentifierAnnotation());
    }

    private Predicate<CtAnnotation> isAggregateIdentifierAnnotation() {
        return annotation -> AXON_AGGREGATE_IDENTIFIER_ANNOTATION.equals(annotation.getActualAnnotation().annotationType().getName());
    }

}