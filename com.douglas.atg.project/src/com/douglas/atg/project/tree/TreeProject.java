/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.douglas.atg.project.AtgProject;
import com.douglas.atg.project.ConfigurationFile;
import com.douglas.atg.project.tree.TreeSubProject.SubProjectType;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class TreeProject extends Tree {

    private final AtgProject project;
    private boolean compiled = false;
    private final TreeSubProject compiledComp;
    private final TreeSubProject components;
    private final TreeSubProject dependencies;

    public TreeProject(final String name, final AtgProject project) {
        super(name, false);
        this.project = project;

        components = new TreeSubProject("Components", SubProjectType.COMPONENTS);
        components.setParent(this);
        this.addChild(components);

        compiledComp = new TreeSubProject("Compiled", SubProjectType.COMPILED_COMPONENTS);
        compiledComp.setParent(this);
        this.addChild(compiledComp);

        dependencies = new TreeSubProject("Dependencies", SubProjectType.DEPENDENCIES);
        dependencies.setParent(this);
        this.addChild(dependencies);
    }

    public AtgProject getProject() {
        return project;
    }

    public TreeSubProject getCompiled() {
        return this.compiledComp;
    }

    public TreeSubProject getComponents() {
        return this.components;
    }

    public TreeSubProject getDependencies() {
        return this.dependencies;
    }

    public List<ConfigurationFile> getAllDependenciesFiles() {
        final List<ConfigurationFile> configurationFiles = new ArrayList<>();
        final Set<TreeProject> projects = getAllProjectDependencies();
        for (final TreeProject project : projects) {
            configurationFiles.addAll(project.getProject().getConfigurationFiles());
        }
        configurationFiles.addAll(this.getProject().getConfigurationFiles());
        return configurationFiles;
    }

    public Set<TreeProject> getAllProjectDependencies() {
        final Set<TreeProject> projects = new HashSet<>();
        for (int i = 0; i < this.dependencies.getChildren().length; i++) {
            final TreeProject project = (TreeProject) this.dependencies.getChildren()[i];
            projects.addAll(project.getAllProjectDependencies());
            projects.add(project);
        }
        return projects;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(final boolean compiled) {
        this.compiled = compiled;
    }

    @Override
    public TreeProject clone() {
        final TreeProject newProject = new TreeProject(getName(), getProject());
        for (final Tree child : this.getChildren()) {
            final Tree newTree = child.clone();
            newTree.setParent(newProject);
            newProject.addChild(newTree);
        }
        return newProject;
    }
}
