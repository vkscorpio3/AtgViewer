package com.douglas.atg.project.views;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.douglas.atg.project.Activator;
import com.douglas.atg.project.preferences.WorkbenchPreferencePage;
import com.douglas.atg.project.tree.Tree;
import com.douglas.atg.project.tree.TreeClass;
import com.douglas.atg.project.tree.TreeCompiledComponent;
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
    private static final ImageDescriptor REFRESH = Activator.getImageDescriptor("icons/refresh.png");

    private TreeViewer viewer;

    //    private DrillDownAdapter drillDownAdapter;
    private Action refreshAction;
    //    private Action action2;
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
        //        drillDownAdapter = new DrillDownAdapter(viewer);
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
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    private void hookContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                AtgProjectsView.this.fillContextMenu(manager);
            }
        });
        final Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        final IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(final IMenuManager manager) {
        manager.add(refreshAction);
        //        manager.add(new Separator());
        //        manager.add(action2);
    }

    private void fillContextMenu(final IMenuManager manager) {
        manager.add(refreshAction);
        //        manager.add(action2);
        //        manager.add(new Separator());
        //        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        //        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(final IToolBarManager manager) {
        manager.add(refreshAction);
        //        manager.add(action2);
        //        manager.add(new Separator());
        //        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        refreshAction = new Action() {
            @Override
            public void run() {
                ((ViewContentProvider) viewer.getContentProvider()).setToReload(true);
                viewer.refresh();
                showMessage("Refresh executed");
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refresh tree content");
        refreshAction.setImageDescriptor(REFRESH);

        //        action2 = new Action() {
        //            @Override
        //            public void run() {
        //                showMessage("Action 2 executed");
        //            }
        //        };
        //        action2.setText("Action 2");
        //        action2.setToolTipText("Action 2 tooltip");
        //        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        doubleClickAction = new Action() {
            @Override
            public void run() {
                final ISelection selection = viewer.getSelection();
                final Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof TreeCompiledComponent) {
                    openCompiled((TreeCompiledComponent) obj);
                } else if (obj instanceof TreeComponent) {
                    final File fileToOpen = ((TreeComponent) obj).getPropertiesFile().getConfigurationFilePath().toFile();
                    openFile(fileToOpen);
                    loadFileInPropertiesView(((TreeComponent) obj).getPropertiesFile().getConfigurationFilePath());
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
     * @param compiled
     */
    private void openCompiled(final TreeCompiledComponent compiled) {
        final Properties props = new Properties();
        final StringBuffer sb = new StringBuffer();
        for (final Tree child : compiled.getChildren()) {
            final TreeComponent comp = (TreeComponent) child;
            try {
                props.load(Files.newInputStream(comp.getPropertiesFile().getConfigurationFilePath()));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
            sb.append("# ***************************************** ");
            sb.append(System.getProperty("line.separator"));
            sb.append("# ");
            sb.append(comp.getPropertiesFile().getConfigurationFilePath().toString());
            sb.append(System.getProperty("line.separator"));
            sb.append("# *****************************************");
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
            getFileContent(sb, comp.getPropertiesFile().getConfigurationFilePath());
        }
        try {
            final Path tempFile = Files.createTempFile(compiled.getName(), ".properties");
            writeContentToFile(sb, tempFile);
            openFile(tempFile.toFile());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        loadInPropertiesView(props);
    }

    /**
     * @param props
     */
    private void loadFileInPropertiesView(final Path file) {
        final Properties props = new Properties();
        try {
            props.load(Files.newInputStream(file));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        loadInPropertiesView(props);
    }

    /**
     * @param props
     */
    private void loadInPropertiesView(final Properties props) {
        final IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(AtgPropertiesView.ID);
        if (part instanceof AtgPropertiesView) {
            final AtgPropertiesView view = (AtgPropertiesView) part;
            view.load(props);
            // now access whatever internals you can get to
        }
    }

    private void getFileContent(final StringBuffer sb, final Path file) {
        // Defaults to READ
        try (SeekableByteChannel sbc = Files.newByteChannel(file)) {
            final ByteBuffer buf = ByteBuffer.allocate(10);

            // Read the bytes with the proper encoding for this platform.  If
            // you skip this step, you might see something that looks like
            // Chinese characters when you expect Latin-style characters.
            final String encoding = System.getProperty("file.encoding");
            while (sbc.read(buf) > 0) {
                buf.rewind();
                sb.append(Charset.forName(encoding).decode(buf));
                buf.flip();
            }
        } catch (final IOException x) {
            System.out.println("caught exception: " + x);
        }
    }

    private void writeContentToFile(final StringBuffer sb, final Path file) {
        // Create the set of options for appending to the file.
        final Set<OpenOption> options = new HashSet<OpenOption>();
        options.add(APPEND);
        options.add(CREATE);

        // Create the custom permissions attribute.
        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r-----");
        final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

        // Convert the string to a ByteBuffer.
        final byte data[] = sb.toString().getBytes();
        final ByteBuffer bb = ByteBuffer.wrap(data);

        try (SeekableByteChannel sbc = Files.newByteChannel(file, options, attr)) {
            sbc.write(bb);
        } catch (final IOException x) {
            System.out.println("Exception thrown: " + x);
        }
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

    private void showMessage(final String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "Atg Projects View", message);
    }

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
            if (WorkbenchPreferencePage.EXCLUDE_PATH_DIR.equals(property) || WorkbenchPreferencePage.PATH.equals(property) || WorkbenchPreferencePage.ENVIRONMENT_NAME.equals(property) || WorkbenchPreferencePage.BASE_MODULE.equals(property)) {
                ((ViewContentProvider) this.viewer.getContentProvider()).setToReload(true);
                this.viewer.refresh();
            }
        }
    }
}