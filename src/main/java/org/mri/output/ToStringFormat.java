package org.mri.output;

import org.mri.AxonNode;

import java.io.PrintStream;

import static java.lang.String.format;

public class ToStringFormat implements OutputFormat {
    @Override
    public void print(AxonNode axonNode, PrintStream output) {
        if (axonNode.hasChildren()) {
            print(axonNode, output, "");
        }
    }

    private void print(AxonNode root, PrintStream output, String indent) {
        output.println(format("%s << %s >>", indent, root.type()));
        output.println(format("%s %s", indent, root.reference().toString()));

        String childrenIndent = indent.concat("\t");
        for (AxonNode child : root.children()) {
            print(child, output, childrenIndent);
        }
    }
}
