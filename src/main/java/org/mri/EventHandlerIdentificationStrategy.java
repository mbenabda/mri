package org.mri;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.List;

public interface EventHandlerIdentificationStrategy {
    List<CtMethodImpl> findEventHandlers(CtTypeReference type);

    boolean isAnEvent(CtTypeReference candidate);
}
