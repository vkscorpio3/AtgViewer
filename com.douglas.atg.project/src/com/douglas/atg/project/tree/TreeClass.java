/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.tree;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class TreeClass extends Tree implements Cloneable {

    private final String className;

    public TreeClass(final String name, final String className) {
        super(name, false);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return getClassName();
    }

    @Override
    public TreeClass clone() {
        return new TreeClass(getName(), getClassName());
    }
}
