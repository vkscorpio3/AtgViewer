/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.preferences;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ExcludeDirectoryDialog extends TitleAreaDialog {
    private Text txtDirectoryName;

    private String directoryName;

    public ExcludeDirectoryDialog(final Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Exclusion Directory Path dialog");
        setMessage("Enter the name of the directory you will not put any project in.", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite area = (Composite) super.createDialogArea(parent);
        final Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createDirectoryName(container);

        return area;
    }

    private void createDirectoryName(final Composite container) {
        final Label lbtDirectoryName = new Label(container, SWT.NONE);
        lbtDirectoryName.setText("Directory Name");

        final GridData dataDirectoryName = new GridData();
        dataDirectoryName.grabExcessHorizontalSpace = true;
        dataDirectoryName.horizontalAlignment = GridData.FILL;

        txtDirectoryName = new Text(container, SWT.BORDER);
        txtDirectoryName.setLayoutData(dataDirectoryName);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        directoryName = txtDirectoryName.getText();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
