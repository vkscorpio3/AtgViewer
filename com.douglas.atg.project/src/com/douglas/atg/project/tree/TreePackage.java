/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.tree;

import java.nio.file.Path;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class TreePackage extends Tree {

    private final Path packagePath;

    public TreePackage(final String name, final Path packagePath) {
        super(name, false);
        this.packagePath = packagePath;
    }

    public Path getPackagePath() {
        return packagePath;
    }

    /**
     * Get the path of the current package with its parent path prepended
     * @return the full path
     */
    public Path getFullPackagePath() {
        if (getParent() != null && getParent() instanceof TreePackage) {
            return ((TreePackage) getParent()).getFullPackagePath().resolve(packagePath);
        }

        return packagePath;
    }

    @Override
    public TreePackage clone() {
        final TreePackage newPackage = new TreePackage(getName(), getPackagePath());
        for (final Tree subtree : this.getChildren()) {
            final Tree newTree = subtree.clone();
            newTree.setParent(newPackage);
            newPackage.addChild(newTree);
        }
        return newPackage;
    }

    /**
     * @return the parent project if it is a TreeProject or the parent of the parent of the current project if...
     */
    public TreeProject getParentProject() {
        if (this.getParent() instanceof TreeProject) {
            return (TreeProject) this.getParent();
        } else if (this.getParent() instanceof TreePackage) {
            return ((TreePackage) this.getParent()).getParentProject();
        } else if (this.getParent() instanceof TreeSubProject) {
            return (TreeProject) this.getParent().getParent();
        }

        return null;
    }
}
