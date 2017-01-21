package org.mri;

import com.google.common.collect.ImmutableList;
import spoon.reflect.reference.CtExecutableReference;

import java.util.ArrayList;
import java.util.List;

public class AxonNode {
    public enum Type {
        CONTROLLER, COMMAND, COMMAND_HANDLER, EVENT, EVENT_LISTENER, AGGREGATE;
    }

    private final Type type;
    private final CtExecutableReference reference;
    private List<AxonNode> children = new ArrayList<>();

    public AxonNode(Type type, CtExecutableReference reference) {
        this.type = type;
        this.reference = reference;
    }

    public void add(AxonNode node) {
        this.children.add(node);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public CtExecutableReference reference() {
        return reference;
    }


    public Type type() {
        return type;
    }

    public List<AxonNode> children() {
        return ImmutableList.copyOf(children);
    }

    public List<AxonNode> descendants() {
        List<AxonNode> all = new ArrayList<>();
        for (AxonNode child : children) {
            all.add(child);
            all.addAll(child.descendants());
        }
        return all;
    }
}
