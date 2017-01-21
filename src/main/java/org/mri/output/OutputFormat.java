package org.mri.output;

import org.mri.AxonNode;

import java.io.PrintStream;

public interface OutputFormat {
    void print(AxonNode root, PrintStream output);
}
