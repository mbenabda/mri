package org.mri.processors;

import org.mri.processors.axon2.AggregatesProcessor;
import org.mri.processors.axon2.CommandHandlersProcessor;
import org.mri.processors.axon2.EventHandlersProcessor;
import org.mri.source.Aggregates;
import org.mri.source.CommandHandlers;
import org.mri.source.eventHandlers.EventHandlers;
import spoon.processing.Processor;

public enum AxonVersion {
    V2 {
        public Processor aggregatesProcessor(Aggregates input) {
            return new AggregatesProcessor(input);
        }

        public Processor commandHandlersProcessor(CommandHandlers input) {
            return new CommandHandlersProcessor(input);
        }

        public Processor eventHandlersProcessor(EventHandlers input) {
            return new EventHandlersProcessor(input);
        }
    },
    V3 {
        public Processor aggregatesProcessor(Aggregates input) {
            return new org.mri.processors.axon3.AggregatesProcessor(input);
        }

        public Processor commandHandlersProcessor(CommandHandlers input) {
            return new org.mri.processors.axon3.CommandHandlersProcessor(input);
        }

        public Processor eventHandlersProcessor(EventHandlers input) {
            return new org.mri.processors.axon3.EventHandlersProcessor(input);
        }
    };

    public Processor eventHandlersProcessor(EventHandlers eventHandlers) {
        throw new UnsupportedOperationException();
    }

    public Processor commandHandlersProcessor(CommandHandlers commandHandlers) {
        throw new UnsupportedOperationException();
    }

    public Processor aggregatesProcessor(Aggregates aggregates) {
        throw new UnsupportedOperationException();
    }
}