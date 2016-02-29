/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.douglas.atg.project.tree.Tree;
import com.douglas.atg.project.tree.TreeClass;
import com.douglas.atg.project.tree.TreeComponent;
import com.douglas.atg.project.tree.TreePackage;
import com.douglas.atg.project.tree.TreeProject;
import com.douglas.atg.project.tree.TreeSubProject;
import com.douglas.atg.project.tree.TreeSubProject.SubProjectType;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ViewLabelProvider extends LabelProvider {

    @Override
    public String getText(final Object obj) {
        return obj.toString();
    }

    @Override
    public Image getImage(final Object obj) {
        Image image = null;
        if (obj instanceof TreeProject) {
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        } else if (obj instanceof TreeComponent) {
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        } else if (obj instanceof TreeSubProject) {
            if (((TreeSubProject) obj).getProjectType() == SubProjectType.DEPENDENCIES) {
                image = org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
            } else if (((TreeSubProject) obj).getProjectType() == SubProjectType.COMPONENTS) {
                image = org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_LOGICAL_PACKAGE);
            } else if (((TreeSubProject) obj).getProjectType() == SubProjectType.COMPILED_COMPONENTS) {
                image = org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_EMPTY_LOGICAL_PACKAGE);
            }
        } else if (obj instanceof TreeClass) {
            image = org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
        } else if (obj instanceof TreePackage) {
            image = org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
        } else if (obj instanceof Tree) {
            image = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
        }
        return image;
    }
}
