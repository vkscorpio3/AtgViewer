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
public class TreeSubProject extends Tree {

    public enum SubProjectType {
        DEPENDENCIES, COMPONENTS, COMPILED_COMPONENTS;
    }

    private final SubProjectType projectType;

    public TreeSubProject(final String name, final SubProjectType projectType) {
        super(name, false);
        this.projectType = projectType;
    }

    public SubProjectType getProjectType() {
        return projectType;
    }

    @Override
    public TreeSubProject clone() {
        final TreeSubProject newProject = new TreeSubProject(getName(), getProjectType());
        for (final Tree child : this.getChildren()) {
            final Tree newTree = child.clone();
            newTree.setParent(newProject);
            newProject.addChild(newTree);
        }
        return newProject;
    }
}
