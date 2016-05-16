/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.douglas.atg.project.tree.TreeProject;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ComparisonHandler extends AbstractHandler {

    /**
     * @param event
     * @return
     * @throws ExecutionException
     */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final Shell shell = HandlerUtil.getActiveShell(event);
        final ISelection sel = HandlerUtil.getActiveMenuSelection(event);
        final IStructuredSelection selection = (IStructuredSelection) sel;

        final Object firstElement = selection.getFirstElement();
        if (firstElement instanceof TreeProject) {
            createOutput(shell, (TreeProject) firstElement);

        } else {
            MessageDialog.openInformation(shell, "Info", "Please select a Project");
        }
        return null;
    }

    /**
     * @param shell
     * @param firstElement
     */
    private void createOutput(final Shell shell, final TreeProject firstElement) {
        MessageDialog.openInformation(shell, "Selected Project", firstElement.getName());

    }

    protected String getPersistentProperty(final IResource res, final QualifiedName qn) {
        try {
            return res.getPersistentProperty(qn);
        } catch (final CoreException e) {
            return "";
        }
    }

    protected void setPersistentProperty(final IResource res, final QualifiedName qn, final String value) {
        try {
            res.setPersistentProperty(qn, value);
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }
}
