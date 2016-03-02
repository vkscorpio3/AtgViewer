/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project;

import java.nio.file.Path;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ConfigurationFile {
    private Path configurationFilePath;
    private Path projectRelativeConfigurationFilePath;

    public ConfigurationFile(final Path configurationFilePath, final Path projectPath) {
        this.setConfigurationFilePath(configurationFilePath);
        this.setProjectRelativeConfigurationFilePath(projectPath.relativize(configurationFilePath));
    }

    public Path getConfigurationFilePath() {
        return configurationFilePath;
    }

    public void setConfigurationFilePath(final Path configurationFilePath) {
        this.configurationFilePath = configurationFilePath;
    }

    public Path getProjectRelativeConfigurationFilePath() {
        return projectRelativeConfigurationFilePath;
    }

    public void setProjectRelativeConfigurationFilePath(final Path projectRelativeConfigurationFilePath) {
        this.projectRelativeConfigurationFilePath = projectRelativeConfigurationFilePath;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return configurationFilePath.toString() + " - " + this.projectRelativeConfigurationFilePath.toString();
    }
}
