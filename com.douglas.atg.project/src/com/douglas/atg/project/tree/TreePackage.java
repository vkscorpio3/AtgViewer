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
     * @param newChild
     * @return
     */
    @Override
    public Tree addChild(final Tree newChild) {
        //        System.out.println("Adding NewChild to a Tree Package (" + this.getName() + ") : " + newChild.getName());
        if (newChild instanceof TreeComponent) {
            TreeComponent childToRemove = null;
            //            System.out.println("    NewChild is TreeComponent, let's see if the current Tree Package Component has some children");
            for (final Tree child : getChildren()) {
                //                System.out.println("    -  ExistingChild found: " + child.getName());
                if (child instanceof TreeCompiledComponent && child.getName().equals(newChild.getName())) {
                    //                    System.out.println("       ExistingChild is TreeCompiledComponent, let's add the new child to this one");
                    return child.addChild(newChild);
                } else if (child instanceof TreeComponent && child.getName().equals(newChild.getName())) {
                    //                    System.out.println("       ExistingChild is TreeComponent, save it before replacing it by a TreeCompiledComponent");
                    childToRemove = (TreeComponent) child;
                }
            }
            //            System.out.println("    Check if a component has to be removed");
            if (childToRemove != null) {
                //                System.out.println("       Remove the component: " + childToRemove.getName());
                removeChild(childToRemove);
                //                System.out.println("       Create the compiled component");
                final TreeCompiledComponent compiled = new TreeCompiledComponent(childToRemove);
                //                System.out.println("       Add NewChild to the compiled component");
                compiled.addChild(newChild);
                //                System.out.println("       Add the compiled component to the TreePackage");
                return super.addChild(compiled);
            }
        }
        //        System.out.println("    Add NewChild to the current TreeComponent");
        return super.addChild(newChild);
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
