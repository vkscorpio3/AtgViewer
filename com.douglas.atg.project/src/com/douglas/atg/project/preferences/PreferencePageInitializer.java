/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.preferences;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.douglas.atg.project.Activator;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class PreferencePageInitializer extends AbstractPreferenceInitializer {

    /**
     * 
     */
    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final IWorkspace wksp = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = wksp.getRoot();

        store.setDefault(WorkbenchPreferencePage.PATH, root.getLocation().toString());
        store.setDefault(WorkbenchPreferencePage.ENVIRONMENT_NAME, "AuxiliarySrv.CN");
        store.setDefault(WorkbenchPreferencePage.BASE_MODULE, "CUBEStore");
        store.setDefault(
                WorkbenchPreferencePage.EXCLUDE_PATH_DIR,
                "bin,src,target,.settingsj2ee-apps,app,environment,views,static-content,node_modules,public,.jazz5,.metadata,assets,database,tomcat-service-layer-server-mock");
    }
}
