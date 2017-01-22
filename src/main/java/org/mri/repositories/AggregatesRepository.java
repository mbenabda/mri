package org.mri.repositories;

import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class AggregatesRepository {
    private List<CtTypeReference> aggregates = null;

    public AggregatesRepository() {
        this.aggregates = new ArrayList<>();
    }

    public List<CtTypeReference> findAll() {
        return aggregates;
    }

    public void add(CtTypeReference aggregateType) {
        aggregates.add(aggregateType);
    }

    public boolean isAnAggregate(CtTypeReference candidate) {
        return aggregates.contains(candidate);
    }
}
