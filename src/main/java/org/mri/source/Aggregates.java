package org.mri.source;

import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class Aggregates {
    private List<CtTypeReference> aggregates = null;

    public Aggregates() {
        this.aggregates = new ArrayList<>();
    }

    public void add(CtTypeReference aggregateType) {
        aggregates.add(aggregateType);
    }

    public boolean isAnAggregate(CtTypeReference candidate) {
        return aggregates.contains(candidate);
    }
}
