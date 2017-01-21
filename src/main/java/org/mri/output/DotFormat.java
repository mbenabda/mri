package org.mri.output;

import org.mri.AxonNode;

import java.io.PrintStream;

public class DotFormat implements OutputFormat {

    @Override
    public void print(AxonNode root, PrintStream output) {
        if(root.hasChildren()) {
            output.println("digraph G {");
            printChildren(output, root);
            output.println("}");
        }
    }

    private void printChildren(PrintStream printStream, AxonNode root) {
        for (AxonNode child : root.children()) {
            printStream.println(
                "\"" + className(root) + "#" + methodName(root) + "\""
                    + " -> "
                    + "\"" + className(child) + "#" + methodName(child) + "\"");
            printChildren(printStream, child);
        }
    }

    private String className(AxonNode node) {
        return node.reference().getDeclaringType().getSimpleName();
    }

    private String methodName(AxonNode node) {
        switch (node.type()) {
            case CONTROLLER:
            case COMMAND_HANDLER:
            case EVENT_LISTENER:
                return "create";
            case COMMAND:
            case EVENT:
            case AGGREGATE:
                return node.reference().getSimpleName();
        }
        return "<<call>>";
    }

}
