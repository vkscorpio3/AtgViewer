/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class ProjectAnalyser {
    private static final String filePattern = "MANIFEST.MF";

    private String initialPath;
    private String baseModuleName;
    private String envName;
    private Path startingDir;

    private final Map<String, AtgProject> projects = new TreeMap<>();

    /**
     * Getter for projects.
     * @return the projects
     */
    public Map<String, AtgProject> getProjects() {
        return projects;
    }

    public String getEnvName() {
        return envName;
    }

    /**
     * Recursive function to manage dependencies of projects
     * @param project the main project
     * @return the list of dependencies
     */
    private List<AtgProject> listDependencies(final AtgProject project) {
        final List<AtgProject> dependencyProjects = new ArrayList<AtgProject>();
        for (final String dependency : project.getDependencyNames()) {
            if (dependency != null && !dependency.trim().isEmpty()) {
                final AtgProject dependencyProject = projects.get(dependency);
                if (dependencyProject != null) {
                    dependencyProjects.add(dependencyProject);
                }
            }
        }

        return dependencyProjects;
    }

    /**
     * 
     */
    public void run(final String initialPath, final String baseModuleName, final String envName, final String[] dirExclusionPattern) {

        this.initialPath = initialPath;
        this.baseModuleName = baseModuleName;
        this.envName = envName;
        this.startingDir = Paths.get(this.initialPath);
        // walk through file system searching for Manifest.mf files
        // avoiding not suitable directories
        final Finder finder = new Finder(filePattern, dirExclusionPattern);
        try {
            Files.walkFileTree(startingDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, finder);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final List<Path> metaFiles = finder.getMetaFiles();

        // building AtgProject from Manifest files
        for (final Path path : metaFiles) {
            final AtgProject project = new AtgProject(path, initialPath, baseModuleName);
            projects.put(project.getProjectName(), project);
        }

        // adding AtgProject dependencies to AtgProjects
        for (final AtgProject project : projects.values()) {
            project.getDependencies().addAll(listDependencies(project));
        }
    }

    public static void main(final String[] args) {
        final ProjectAnalyser prj = new ProjectAnalyser();
        // default values from the properties file
        final Properties prop = new Properties();
        try {
            final InputStream inStream = prop.getClass().getClassLoader().getResourceAsStream("com/douglas/atg/project/configuration.properties");
            prop.load(inStream);
            inStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        String initialPath = (String) prop.get("path");
        String baseModuleName = (String) prop.get("base.module.name");
        String envName = (String) prop.get("env.name");

        // parameters are given, using them
        if (args.length == 3) {
            initialPath = args[0];
            baseModuleName = args[1];
            envName = args[2];
        }
        prj.run(initialPath, baseModuleName, envName, new String[] {"**/bin", "**/src", "**/target", "**/.settings", "**/j2ee-apps", "**/app", "**/environment", "**/views", "**/static-content", "**/node_modules", "**/public", "**/.jazz5",
                "**/.metadata", "**/assets", "**/database", "**/tomcat-service-layer-server-mock" });
    }
}
