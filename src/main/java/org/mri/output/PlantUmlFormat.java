package org.mri.output;

import com.google.common.collect.Lists;
import org.mri.AxonNode;

import java.io.PrintStream;
import java.util.List;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static java.lang.String.format;

public class PlantUmlFormat implements OutputFormat {

    @Override
    public void print(AxonNode root, PrintStream output) {
        if (root.hasChildren()) {
            output.println("@startuml " + LOWER_CAMEL.to(LOWER_HYPHEN, root.reference().getSimpleName()) + "-flow.png");
            List<AxonNode> all = Lists.newArrayList(root);
            all.addAll(root.descendants());
            for (AxonNode each : all) {
                output.println("participant \"" + prettyActorName(each) + "\" as " + actorName(each));
            }
            output.println();
            printPlantUMLComponent(root, output);
            output.println("@enduml");
        }
    }

    private String prettyActorName(AxonNode node) {
        return node.reference().getDeclaringType().getPackage().getSimpleName() + "\\n" + "**" + node.reference().getDeclaringType().getSimpleName() + "**";
    }

    private String actorName(AxonNode node) {
        return node.reference().getDeclaringType().getPackage().getSimpleName() + "." + node.reference().getDeclaringType().getSimpleName();
    }

    private void printPlantUMLComponent(AxonNode root, PrintStream output) {
        for (AxonNode child : root.children()) {
            output.println(format(
                "%s %s %s: %s",
                actorName(root),
                transition(root),
                actorName(child),
                methodName(child)
            ));

            printPlantUMLComponent(child, output);
        }
    }

    private String transition(AxonNode node) {
        switch (node.type()) {
            case CONTROLLER:
            case COMMAND_HANDLER:
            case EVENT_LISTENER:
            case AGGREGATE:
                return "->";
            case COMMAND:
            case EVENT:
                return "-->";
        }
        return "->";
    }
    private String methodName(AxonNode child) {
        switch (child.type()) {
            case CONTROLLER:
            case COMMAND_HANDLER:
            case EVENT_LISTENER:
                return "create";
            case COMMAND:
            case EVENT:
            case AGGREGATE:
                return child.reference().getSimpleName();
        }
        return "<<call>>";
    }
}
