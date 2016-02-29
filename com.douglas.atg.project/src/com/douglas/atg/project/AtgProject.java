/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class AtgProject implements Comparable<AtgProject> {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private AtgMetaInf metaInf;
    private String projectName;
    private final Path initialPath;
    private final Path initConfigPath;
    private List<ConfigurationFile> configurationFiles = new ArrayList<>();
    private List<AtgProject> dependencies = new ArrayList<>();
    private String atgConfigPath = null;

    public AtgProject(final Path path, final String initialPath, final String baseModuleName) {
        this.setMetaInf(new AtgMetaInf(path));
        String correctedInitialPath = initialPath;
        if (!correctedInitialPath.endsWith("/")) {
            correctedInitialPath += "/";
        }
        this.initialPath = Paths.get(correctedInitialPath);
        setProjectName(baseModuleName + "." + path.toString().replaceFirst(initialPath, "").replace("/configuration", "").replace("/META-INF/MANIFEST.MF", "").replace('/', '.'));

        for (final String configPath : this.metaInf.getAtgConfigPath()) {
            if (configPath != null && !configPath.trim().isEmpty()) {
                atgConfigPath = configPath;
                break;
            }
        }

        if (this.metaInf != null && this.metaInf.getPath() != null && this.metaInf.getPath().getParent() != null && this.metaInf.getPath().getParent().getParent() != null && atgConfigPath != null) {
            initConfigPath = this.metaInf.getPath().getParent().getParent().resolve(atgConfigPath);
        } else {
            initConfigPath = this.initialPath;
        }

        fillConfigurationFiles();
    }

    public List<String> getDependencyNames() {
        return metaInf.getAtgRequired();
    }

    /**
     * Getter for metaInf.
     * @return the metaInf
     */
    public AtgMetaInf getMetaInf() {
        return metaInf;
    }

    /**
     * Setter for metaInf.
     * @param metaInf the metaInf to set
     */
    public void setMetaInf(final AtgMetaInf metaInf) {
        this.metaInf = metaInf;
    }

    /**
     * Getter for projectName.
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Setter for projectName.
     * @param projectName the projectName to set
     */
    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public Path getInitConfigPath() {
        return initConfigPath;
    }

    /**
     * Getter for configurationFiles.
     * @return the configurationFiles
     */
    public List<ConfigurationFile> getConfigurationFiles() {
        return configurationFiles;
    }

    /**
     * Setter for configurationFiles.
     * @param configurationFiles the configurationFiles to set
     */
    public void setConfigurationFiles(final List<ConfigurationFile> configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Getter for dependencies.
     * @return the dependencies
     */
    public List<AtgProject> getDependencies() {
        return dependencies;
    }

    /**
     * Setter for dependencies.
     * @param dependencies the dependencies to set
     */
    public void setDependencies(final List<AtgProject> dependencies) {
        this.dependencies = dependencies;
    }

    public List<ConfigurationFile> getAllConfigurationFiles() {
        final List<ConfigurationFile> paths = new ArrayList<>();

        if (dependencies != null && !dependencies.isEmpty()) {
            for (final AtgProject project : dependencies) {
                paths.addAll(project.getAllConfigurationFiles());
            }
        }

        paths.addAll(configurationFiles);

        return paths;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("****** Project Name: ").append(this.projectName).append(NEW_LINE).append(this.metaInf.toString());
        return sb.toString();
    }

    /**
     * Compare project by their name.
     * @param project other project
     * @return comparison result
     */
    @Override
    public int compareTo(final AtgProject project) {
        return this.projectName.compareTo(project.getProjectName());
    }

    /**
     * 
     */
    private void fillConfigurationFiles() {
        final Path projectPath = metaInf.getPath().getParent().getParent();
        // *.{java,class}}
        final Finder finder = new Finder("*.*", new String[] {"**/config-cache", "**/configuration" });

        for (final String configPath : metaInf.getAtgConfigPath()) {
            if (configPath != null && configPath.trim().length() > 0) {
                try {
                    if (projectPath.resolve(configPath).toFile().exists()) {
                        Files.walkFileTree(projectPath.resolve(configPath), finder);
                        for (final Path metaFile : finder.getMetaFiles()) {
                            getConfigurationFiles().add(new ConfigurationFile(metaFile, projectPath.resolve(configPath)));
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
