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
public class TreeCompiledComponent extends Tree {

    /**
     * @param name
     * @param root
     */
    public TreeCompiledComponent(final String name, final boolean root) {
        super(name, root);
    }

    public TreeCompiledComponent(final TreeComponent comp) {
        super(comp != null ? comp.getName() : "", comp != null ? comp.isRoot() : false);
        this.addChild(comp);
    }
}
