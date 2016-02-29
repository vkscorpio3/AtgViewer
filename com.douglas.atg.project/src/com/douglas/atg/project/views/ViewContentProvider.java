/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.views;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

import com.douglas.atg.project.Activator;
import com.douglas.atg.project.AtgProject;
import com.douglas.atg.project.ConfigurationFile;
import com.douglas.atg.project.ProjectAnalyser;
import com.douglas.atg.project.preferences.WorkbenchPreferencePage;
import com.douglas.atg.project.tree.Tree;
import com.douglas.atg.project.tree.TreeComponent;
import com.douglas.atg.project.tree.TreePackage;
import com.douglas.atg.project.tree.TreeProject;
import com.douglas.atg.project.tree.TreeSubProject;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    private Tree invisibleRoot;
    private final IViewSite viewSite;
    private boolean toReload;
    private final ProjectAnalyser prj = new ProjectAnalyser();

    public boolean isToReload() {
        return toReload;
    }

    public void setToReload(final boolean toReload) {
        this.toReload = toReload;
    }

    @Override
    public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
    }

    @Override
    public void dispose() {
    }

    public ViewContentProvider(final IViewSite viewSite) {
        this.viewSite = viewSite;
    }

    @Override
    public Object[] getElements(final Object parent) {
        if (parent.equals(viewSite)) {
            if (invisibleRoot == null || isToReload()) {
                initialize();
            }
            return getChildren(invisibleRoot);
        }
        return getChildren(parent);
    }

    @Override
    public Object getParent(final Object child) {
        if (child instanceof TreeProject) {
            return ((TreeProject) child).getParent();
        }
        return null;
    }

    @Override
    public Object[] getChildren(final Object parent) {
        if (parent instanceof Tree) {
            return ((Tree) parent).getChildren();
        }
        return new Object[0];
    }

    @Override
    public boolean hasChildren(final Object parent) {
        if (parent instanceof Tree) {
            return ((Tree) parent).hasChildren();
        }
        return false;
    }

    protected String[] parseString(final String stringList) {
        final List<String> list = Arrays.asList(stringList.split(","));
        final List<String> dirList = new ArrayList<>();
        for (final Iterator<String> it = list.iterator(); it.hasNext();) {
            final String str = it.next();
            if (str != null && !str.trim().isEmpty()) {
                dirList.add("**/" + str);
            }
        }
        return dirList.toArray(new String[0]);
    }

    /*
     * We will set up a dummy model to initialize tree hierarchy.
     * In a real code, you will connect to a real model and
     * expose its hierarchy.
     */
    private void initialize() {
        final long startTime = new Date().getTime();
        final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        final String initialPath = preferenceStore.getString(WorkbenchPreferencePage.PATH);
        final String envName = preferenceStore.getString(WorkbenchPreferencePage.ENVIRONMENT_NAME);
        final String baseModule = preferenceStore.getString(WorkbenchPreferencePage.BASE_MODULE);
        final String[] excludePathDir = parseString(preferenceStore.getString(WorkbenchPreferencePage.EXCLUDE_PATH_DIR));

        prj.run(initialPath, baseModule, envName, excludePathDir);
        final long afterRunTime = new Date().getTime();

        invisibleRoot = new Tree("", true);
        final Tree root = loadData(baseModule);
        invisibleRoot.addChild(root);
        final long afterLoadTime = new Date().getTime();

        System.out.println(MessageFormat.format("start: {0}, run: {1} sec, load: {2} sec", startTime, (afterRunTime - startTime) / 1000d, (afterLoadTime - afterRunTime) / 1000d));
    }

    /**
     * @param baseModule
     */
    private Tree loadData(final String baseModule) {
        final Tree root = new Tree(baseModule, true);

        final Map<String, TreeProject> projectMap = new HashMap<String, TreeProject>();

        addComponents(root, projectMap);

        addDependencies(projectMap);

        compileComponents(projectMap);

        return root;
    }

    /**
     * Add projects to the Tree root node by going through the AtgProjects in the {@link ProjectAnalyser#getProjects()}
     * @param root root node of the Tree
     * @param projectMap map of TreeProject to add all created projects to
     */
    private void addComponents(final Tree root, final Map<String, TreeProject> projectMap) {
        // go through the all projects
        for (final Entry<String, AtgProject> entry : prj.getProjects().entrySet()) {

            // create a new AtgProject for each project
            final TreeProject to = new TreeProject(entry.getKey(), entry.getValue());
            projectMap.put(entry.getValue().getProjectName(), to);

            populateTreeWithComponents(to.getComponents(), entry.getValue().getConfigurationFiles(), entry.getValue().getInitConfigPath());

            // add current TreeProject to the main Tree Node
            to.setParent(root);
            root.addChild(to);
        }
    }

    /**
     * Adding the sub tree elements generated from configurations files to the components Tree Node.
     * @param components the Components Tree Node to add elements to
     * @param configurationFiles list of all Configuration files
     * @param initialPath initial path to make the configuration relative from this one. Basically used to keep only the package path
     */
    private void populateTreeWithComponents(final TreeSubProject components, final List<ConfigurationFile> configurationFiles, final Path initialPath) {
        final Map<String, TreePackage> packageMap = new HashMap<String, TreePackage>();

        if (components.hasChildren()) {
            packageMap.putAll(getTreePackages(components));
        }

        // go through all Properties files
        for (final ConfigurationFile tree : configurationFiles) {
            // get the properties file relative path from the project root directory
            //final Path relativeTree = initialPath.relativize(tree);
            final Path relativeTree = tree.getProjectRelativeConfigurationFilePath();
            // path to the current element from the project path
            Path cumulatedTree = Paths.get("");
            // set the parent as initial element
            Tree parent = components;

            // walking through the path of the properties file
            final Iterator<Path> it = relativeTree.iterator();
            while (it.hasNext()) {
                final Path path = it.next();

                // if the current path is the properties file then we don't do anything
                if (!path.getFileName().toString().equalsIgnoreCase(relativeTree.getFileName().toString())) {

                    // add the current path to the cumulated one
                    cumulatedTree = cumulatedTree.resolve(path);

                    TreePackage currentPackage = null;
                    // if the package name already exists in the map
                    if (packageMap.containsKey(cumulatedTree.toString())) {
                        // we use it
                        currentPackage = packageMap.get(cumulatedTree.toString());
                    } else {
                        // otherwise we create a new one and add it to the map
                        currentPackage = new TreePackage(path.getFileName().toString(), path);
                        currentPackage.setParent(parent);
                        parent.addChild(currentPackage);
                        packageMap.put(cumulatedTree.toString(), currentPackage);
                    }
                    parent = currentPackage;
                }
            }

            // create the properties file project to add to the tree
            final TreeComponent prj = new TreeComponent(relativeTree.getFileName().toString(), relativeTree);
            prj.setParent(parent);
            // add the leaf to the branch
            parent.addChild(prj);
        }
    }

    /**
     * Create a map with all TreePackages from a TreeSubProject.
     * @param components
     * @return
     */
    private Map<? extends String, ? extends TreePackage> getTreePackages(final TreeSubProject components) {
        final Map<String, TreePackage> packageMap = new HashMap<String, TreePackage>();
        for (final Tree node : components.getChildren()) {
            if (node != null && node instanceof TreePackage) {
                final TreePackage pack = (TreePackage) node;
                packageMap.put(pack.getFullPackagePath().toString(), pack);
                if (pack.hasChildren()) {
                    packageMap.putAll(getChildrenPackages(pack));
                }
            }
        }
        return packageMap;
    }

    /**
     * Recursive function to populate the map
     * @param pack package element
     * @return the children map
     */
    private Map<? extends String, ? extends TreePackage> getChildrenPackages(final Tree parent) {
        final Map<String, TreePackage> packageMap = new HashMap<String, TreePackage>();
        if (parent instanceof TreePackage) {
            for (final Tree node : parent.getChildren()) {
                if (node != null && node instanceof TreePackage) {
                    final TreePackage pack = (TreePackage) node;
                    packageMap.put(pack.getFullPackagePath().toString(), pack);
                    if (pack.hasChildren()) {
                        packageMap.putAll(getChildrenPackages(pack));
                    }
                }
            }
        }
        return packageMap;
    }

    /**
     * Add a new node to each project and fill it with the listed in the {@link AtgProject#getDependencies()} of the {@link TreeProject#getProject()}.
     * @param projectMap map of of projects
     */
    private void addDependencies(final Map<String, TreeProject> projectMap) {
        // go through the project one more time
        for (final AtgProject entry : prj.getProjects().values()) {
            // get the specific node for the Dependencies
            final TreeSubProject sub = projectMap.get(entry.getProjectName()).getDependencies();
            // for each AtgProject, the current project depends on
            for (final AtgProject child : entry.getDependencies()) {
                // add the AtgProject Tree representation as child
                sub.addChild(projectMap.get(child.getProjectName()));
                // we don't set the parent because it already has one;
            }
        }
    }

    /**
     * Get All components from a project and its dependency projects and Compile them
     * @param projectMap
     */
    private void compileComponents(final Map<String, TreeProject> projectMap) {
        /*
        for (final TreeProject node : projectMap.values()) {
            compileProject(node, "");
        }
        */
        compileProject(projectMap.get("CUBEStore.cube-atg-etl"), "");
    }

    /**
     * Fill the current project Compiled node with the sub-projects Components.<br />
     * Recursive function.
     * @param project project to fill
     * @param depth 
     */
    private void compileProject(final TreeProject project, final String depth) {
        if (project != null) {
            // If the project is not yet compiled
            if (!project.isCompiled()) {
                // If the project has dependencies
                if (project.getDependencies() != null && project.getDependencies().hasChildren()) {
                    // For every project the current project depends on
                    for (final Tree child : project.getDependencies().getChildren()) {
                        if (child instanceof TreeProject) {
                            final TreeProject childProject = (TreeProject) child;
                            // If the current dependent project is not yet compiled the we compile them
                            if (!childProject.isCompiled()) {
                                compileProject(childProject, depth + "  ");
                            }
                            // populateTreeWithComponents(project.getCompiled(), configurationFiles, childProject.getProject().getInitConfigPath());
                        }
                    }
                }

                populateTreeWithComponents(project.getCompiled(), project.getAllDependenciesFiles(), project.getProject().getInitConfigPath());
                project.setCompiled(true);
            }
        }
    }

    /**
     * Clone a Tree object calling the {@link Tree#clone()} method on each child. Method are overloaded on every child.
     * @param trees array of Tree node
     * @return cloned array
     */
    private Tree[] cloneTree(final Tree[] trees) {
        final Tree[] forest = new Tree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            forest[i] = trees[i].clone();
        }
        return forest;
    }
}
