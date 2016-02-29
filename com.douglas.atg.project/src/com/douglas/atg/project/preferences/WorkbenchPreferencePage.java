/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.douglas.atg.project.Activator;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     * 
     */
    public static final String EXCLUDE_PATH_DIR = "EXCLUDE_PATH_DIR";
    /**
     * 
     */
    public static final String BASE_MODULE = "BASE_MODULE";
    /**
     * 
     */
    public static final String ENVIRONMENT_NAME = "ENVIRONMENT_NAME";
    /**
     * 
     */
    public static final String PATH = "PATH";

    public WorkbenchPreferencePage() {
        super(GRID);
    }

    /**
     * path=/opt/dev/jazzws/CUBE-Dev/
     * env.name=AuxiliarySrv.CN
     * base.module.name=CUBEStore
     */
    @Override
    public void createFieldEditors() {
        addField(new DirectoryFieldEditor(PATH, "&Directory preference:", getFieldEditorParent()));
        addField(new StringFieldEditor(ENVIRONMENT_NAME, "Name of the environment:", getFieldEditorParent()));
        addField(new StringFieldEditor(BASE_MODULE, "Base module:", getFieldEditorParent()));
        addField(new ListEditor(EXCLUDE_PATH_DIR, "Directory path to exclude from search :", getFieldEditorParent()) {

            @Override
            protected String[] parseString(final String stringList) {
                final List<String> list = new ArrayList<>(Arrays.asList(stringList.split(",")));
                if (!list.isEmpty()) {
                    for (final Iterator<String> it = list.iterator(); it.hasNext();) {
                        final String str = it.next();
                        if (str == null || str.trim().isEmpty()) {
                            it.remove();
                        }
                    }
                }
                return list.toArray(new String[0]);
            }

            @Override
            protected String getNewInputObject() {
                final ExcludeDirectoryDialog dialog = new ExcludeDirectoryDialog(getShell());
                dialog.create();
                if (dialog.open() == Window.OK) {
                    return dialog.getDirectoryName();
                }
                return "";
            }

            @Override
            protected String createList(final String[] items) {
                final StringBuilder sb = new StringBuilder();
                if (items.length > 0) {
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] != null && !items[i].trim().isEmpty()) {
                            sb.append(items[i]);
                            if (i < items.length - 1) {
                                sb.append(",");
                            }
                        }
                    }
                }
                return sb.toString();
            }
        });
    }

    @Override
    public void init(final IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Parameters needed by the Oracle Commerce Projec Veiwer");
    }
}
