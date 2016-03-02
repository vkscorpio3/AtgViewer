/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class Tree implements IAdaptable {
    private final boolean root;
    private final String name;
    private final List<Tree> children;
    private Tree parent;

    public Tree(final String name, final boolean root) {
        this.name = name;
        this.root = root;
        children = new ArrayList<>();
    }

    public boolean isRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public Tree getParent() {
        if (parent != null) {
            return parent;
        } else {
            return parent;
        }
    }

    public void setParent(final Tree parent) {
        this.parent = parent;
    }

    public Tree addChild(final Tree child) {
        children.add(child);
        child.setParent(this);
        return child;
    }

    public void addChildren(final Tree[] children) {
        for (final Tree tree : children) {
            addChild(tree);
        }
    }

    public void removeChild(final Tree child) {
        children.remove(child);
        child.setParent(null);
    }

    public void clearChildren() {
        children.clear();
    }

    public Tree[] getChildren() {
        return children.toArray(new Tree[children.size()]);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Tree clone() {
        final Tree newProject = new Tree(getName(), isRoot());
        for (final Tree child : this.getChildren()) {
            final Tree newTree = child.clone();
            newTree.setParent(newProject);
            newProject.addChild(newTree);
        }
        return newProject;
    }

    /**
     * @param adapter
     * @return
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        return null;
    }
}
