package com.douglas.atg.project.views;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.douglas.atg.project.Activator;
import com.douglas.atg.project.preferences.WorkbenchPreferencePage;
import com.douglas.atg.project.tree.TreeClass;
import com.douglas.atg.project.tree.TreeComponent;
import com.douglas.atg.project.tree.TreeProject;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class AtgProjectsView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.douglas.atg.project.views.AtgProjectsView";

    private TreeViewer viewer;

    //	private DrillDownAdapter drillDownAdapter;
    //	private Action action1;
    //	private Action action2;
    private Action doubleClickAction;

    /**
     * The constructor.
     */
    public AtgProjectsView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        //		drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider(getViewSite()));
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());

        final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        // reload if preferences are changed
        preferenceStore.addPropertyChangeListener(new PreferencesChangeListener(viewer));

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.douglas.atg.project.viewer");
        makeActions();
        //		hookContextMenu();
        hookDoubleClickAction();
        //		contributeToActionBars();
    }

    //	private void hookContextMenu() {
    //		MenuManager menuMgr = new MenuManager("#PopupMenu");
    //		menuMgr.setRemoveAllWhenShown(true);
    //		menuMgr.addMenuListener(new IMenuListener() {
    //			public void menuAboutToShow(IMenuManager manager) {
    //				AtgProjectsView.this.fillContextMenu(manager);
    //			}
    //		});
    //		Menu menu = menuMgr.createContextMenu(viewer.getControl());
    //		viewer.getControl().setMenu(menu);
    //		getSite().registerContextMenu(menuMgr, viewer);
    //	}

    //	private void contributeToActionBars() {
    //		IActionBars bars = getViewSite().getActionBars();
    //		fillLocalPullDown(bars.getMenuManager());
    //		fillLocalToolBar(bars.getToolBarManager());
    //	}

    //	private void fillLocalPullDown(IMenuManager manager) {
    //		manager.add(action1);
    //		manager.add(new Separator());
    //		manager.add(action2);
    //	}

    //	private void fillContextMenu(IMenuManager manager) {
    //		manager.add(action1);
    //		manager.add(action2);
    //		manager.add(new Separator());
    //		drillDownAdapter.addNavigationActions(manager);
    //		// Other plug-ins can contribute there actions here
    //		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    //	}

    //	private void fillLocalToolBar(IToolBarManager manager) {
    //		manager.add(action1);
    //		manager.add(action2);
    //		manager.add(new Separator());
    //		drillDownAdapter.addNavigationActions(manager);
    //	}

    private void makeActions() {
        //		action1 = new Action() {
        //			public void run() {
        //				showMessage("Action 1 executed");
        //			}
        //		};
        //		action1.setText("Action 1");
        //		action1.setToolTipText("Action 1 tooltip");
        //		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        //			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        //		
        //		action2 = new Action() {
        //			public void run() {
        //				showMessage("Action 2 executed");
        //			}
        //		};
        //		action2.setText("Action 2");
        //		action2.setToolTipText("Action 2 tooltip");
        //		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        //				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction = new Action() {
            @Override
            public void run() {
                final ISelection selection = viewer.getSelection();
                final Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof TreeComponent) {
                    final File fileToOpen = ((TreeComponent) obj).getPropertiesFile().toFile();
                    openFile(fileToOpen);
                } else if (obj instanceof TreeProject) {
                    final File fileToOpen = ((TreeProject) obj).getProject().getMetaInf().getPath().toFile();
                    openFile(fileToOpen);
                } else if (obj instanceof TreeClass) {
                    final String classToOpen = ((TreeClass) obj).getClassName();
                    openClass(classToOpen);
                }
            }
        };
    }

    /**
     * Open a file outside the Workspace
     * @param fileToOpen the file
     */
    private void openClass(final String classToOpen) {
        final IWorkspace wksp = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = wksp.getRoot();
        final IProject[] projects = root.getProjects();
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        for (final IProject project : projects) {
            System.out.println("project " + project.getName());
            IJavaProject javaProject = null;
            if (project instanceof IJavaProject) {
                //                System.out.println("Java Project");
                javaProject = (IJavaProject) project;
            } else {
                //                System.out.println("Create Java Project");
                final IJavaProject jProject = JavaCore.create(project);
                javaProject = jProject;
            }

            if (javaProject != null) {
                try {
                    final IType type = javaProject.findType(classToOpen);
                    final IPath path = type.getPath();
                    //                    System.out.println("Path " + path.toString());
                    final String projectPath = path.toString().replace("/" + project.getName(), "");
                    if (!"class".equalsIgnoreCase(path.getFileExtension())) {
                        final IFile file = project.getFile(projectPath);
                        IDE.openEditor(page, file);
                        break;
                    }
                } catch (final JavaModelException e) {
                    e.printStackTrace();
                } catch (final PartInitException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Open a file outside the Workspace
     * @param fileToOpen the file
     */
    private void openFile(final File fileToOpen) {
        if (fileToOpen.exists() && fileToOpen.isFile()) {
            final IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
            final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            try {
                IDE.openEditorOnFileStore(page, fileStore);
            } catch (final PartInitException e) {
                //Put your exception handler here if you wish to
            }
        } else {
            //Do something if the file does not exist
        }
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(final DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    //    private void showMessage(final String message) {
    //        MessageDialog.openInformation(viewer.getControl().getShell(), "Atg Projects View", message);
    //    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    private class PreferencesChangeListener implements IPropertyChangeListener {

        private final TreeViewer viewer;

        /**
         * @param preferenceStore
         * @param viewContentProvider
         */
        public PreferencesChangeListener(final TreeViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final String property = event.getProperty();
            if (WorkbenchPreferencePage.EXCLUDE_PATH_DIR.equals(property) || WorkbenchPreferencePage.PATH.equals(property)
                    || WorkbenchPreferencePage.ENVIRONMENT_NAME.equals(property) || WorkbenchPreferencePage.BASE_MODULE.equals(property)) {
                ((ViewContentProvider) this.viewer.getContentProvider()).setToReload(true);
                this.viewer.refresh();
            }
        }
    }
}