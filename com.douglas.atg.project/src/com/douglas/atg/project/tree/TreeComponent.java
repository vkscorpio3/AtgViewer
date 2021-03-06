/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.tree;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import com.douglas.atg.project.ConfigurationFile;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class TreeComponent extends Tree {

    private final ConfigurationFile propertiesFile;
    private final String componentName;

    public TreeComponent(final String name, final ConfigurationFile file) {
        super(name, false);
        this.propertiesFile = file;
        this.componentName = file.getConfigurationFilePath().getFileName().toString().substring(0, file.getConfigurationFilePath().getFileName().toString().lastIndexOf('.'));

        List<String> lines;
        String className = null;
        try {
            lines = Files.readAllLines(file.getConfigurationFilePath(), Charset.defaultCharset());
            for (final String line : lines) {
                if (line.startsWith("$class=")) {
                    className = line.substring("$class=".length());
                    break;
                }
            }
        } catch (final IOException e) {
            System.err.println(e.getClass().getName() + " " + e.getMessage());
        }

        if (className != null) {
            final TreeClass treeClass = new TreeClass(this.componentName, className);
            treeClass.setParent(this);
            this.addChild(treeClass);
        }
    }

    public ConfigurationFile getPropertiesFile() {
        return propertiesFile;
    }

    public String getComponentName() {
        return componentName;
    }

    public TreeProject getParentProject() {
        if (this.getParent() instanceof TreeProject) {
            return (TreeProject) this.getParent();
        } else if (this.getParent() instanceof TreePackage) {
            return ((TreePackage) this.getParent()).getParentProject();
        } else if (this.getParent() instanceof TreeSubProject) {
            return (TreeProject) this.getParent().getParent();
        }

        return null;
    }

    /**
     * @return
     */
    public String getFullComponentName() {
        final StringBuilder sb = new StringBuilder();
        if (getParent() instanceof TreePackage) {
            sb.append(((TreePackage) getParent()).getFullPackagePath());
            sb.append("/");
        }
        sb.append(toString());
        return sb.toString();
    }

    @Override
    public String toString() {
        // Get the name and not the component name to have the file extension
        return getName();
    }

    public String toExtendedString() {
        // Get the name and not the component name to have the file extension
        return getName() + " " + this.propertiesFile.toString();
    }

    @Override
    public TreeComponent clone() {
        return new TreeComponent(getName(), getPropertiesFile());
    }
}
