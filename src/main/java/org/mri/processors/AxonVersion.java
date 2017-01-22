package org.mri.processors;

import org.mri.processors.axon2.AggregatesProcessor;
import org.mri.processors.axon2.CommandHandlersProcessor;
import org.mri.processors.axon2.EventHandlersProcessor;
import org.mri.repositories.AggregatesRepository;
import org.mri.repositories.CommandHandlersRepository;
import org.mri.repositories.eventHandlers.EventHandlersRepository;
import spoon.processing.Processor;

public enum AxonVersion {
    V2 {
        public Processor aggregatesProcessor(AggregatesRepository input) {
            return new AggregatesProcessor(input);
        }

        public Processor commandHandlersProcessor(CommandHandlersRepository input) {
            return new CommandHandlersProcessor(input);
        }

        public Processor eventHandlersProcessor(EventHandlersRepository input) {
            return new EventHandlersProcessor(input);
        }
    },
    V3 {
        public Processor aggregatesProcessor(AggregatesRepository input) {
            return new org.mri.processors.axon3.AggregatesProcessor(input);
        }

        public Processor commandHandlersProcessor(CommandHandlersRepository input) {
            return new org.mri.processors.axon3.CommandHandlersProcessor(input);
        }

        public Processor eventHandlersProcessor(EventHandlersRepository input) {
            return new org.mri.processors.axon3.EventHandlersProcessor(input);
        }
    };

    public Processor eventHandlersProcessor(EventHandlersRepository eventHandlers) {
        throw new UnsupportedOperationException();
    }

    public Processor commandHandlersProcessor(CommandHandlersRepository commandHandlers) {
        throw new UnsupportedOperationException();
    }

    public Processor aggregatesProcessor(AggregatesRepository aggregates) {
        throw new UnsupportedOperationException();
    }
}