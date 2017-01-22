package org.mri.processors;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.mri.repositories.AggregatesRepository;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.support.reflect.declaration.CtFieldImpl;

public class AggregatesProcessor extends AbstractProcessor<CtFieldImpl> {
    private static final String AXON_AGGREGATE_IDENTIFIER_ANNOTATION = "@org.axonframework.eventsourcing.annotation.AggregateIdentifier";
    private AggregatesRepository repository;

    public AggregatesProcessor(AggregatesRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(CtFieldImpl field) {
        if (isAggregateIdentifier(field)) {
            repository.add(field.getDeclaringType().getReference());
        }
    }

    private boolean isAggregateIdentifier(CtFieldImpl field) {
        return Iterables.tryFind(
            field.getAnnotations(),
            isAggregateIdentifierAnnotation()
        ).isPresent();
    }

    private Predicate<CtAnnotation> isAggregateIdentifierAnnotation() {
        return new Predicate<CtAnnotation>() {
            @Override
            public boolean apply(CtAnnotation annotation) {
                return AXON_AGGREGATE_IDENTIFIER_ANNOTATION.equals(annotation.getSignature());
            }
        };
    }

}