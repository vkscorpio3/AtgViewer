/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.douglas.atg.project.tree.Tree;
import com.douglas.atg.project.tree.TreeComponent;
import com.douglas.atg.project.tree.TreePackage;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class NameSorter extends ViewerSorter {

    /**
     * @param viewer
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(final Viewer viewer, final Object o1, final Object o2) {
        int returnValue;
        if (o1 == null || o2 == null || (o1 == null && o2 == null)) {
            returnValue = 0;
        } else {
            if (o1 instanceof Tree && o2 instanceof Tree) {
                final Tree t1 = (Tree) o1;
                final Tree t2 = (Tree) o2;
                if (t1.getName() == null && t2.getName() == null) {
                    returnValue = 0;
                } else if (t1.getName() == null && t2.getName() != null) {
                    returnValue = 1;
                } else if (t1.getName() != null && t2.getName() == null) {
                    returnValue = -1;
                } else if (t2 instanceof TreePackage && t1 instanceof TreeComponent) {
                    returnValue = 1;
                } else if (t1 instanceof TreePackage && t2 instanceof TreeComponent) {
                    returnValue = -1;
                } else {
                    returnValue = t1.getName().compareTo(t2.getName());
                }
            } else {
                returnValue = super.compare(viewer, o1, o2);
            }
        }
        return returnValue;
    }
}
