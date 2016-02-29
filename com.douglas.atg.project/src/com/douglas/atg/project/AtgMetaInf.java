/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 * Manifest-Version: 1.0
 * ATG-Required: DAS CUBEStore.persistence CUBEStore.service-layer
 * ATG-Client-Class-Path: lib/classes.jar
 * ATG-Class-Path: lib/classes.jar
 * ATG-Config-Path: config/ config-cache/
 * 
 * Name: lib/classes.jar
 * ATG-Client-Update-File: true
 * ATG-Client-Update-Version: 0001
 *
 */
public class AtgMetaInf implements Comparable<AtgMetaInf> {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String MANIFEST_VERSION = "Manifest-Version";
    private static final String ATG_REQUIRED = "ATG-Required";
    private static final String ATG_CLIENT_CLASS_PATH = "ATG-Client-Class-Path";
    private static final String ATG_CLASS_PATH = "ATG-Class-Path";
    private static final String ATG_CONFIG_PATH = "ATG-Config-Path";
    private static final String NAME = "Name";
    private static final String ATG_CLIENT_UPDATE_FILE = "ATG-Client-Update-File";
    private static final String ATG_CLIENT_UPDATE_VERSION = "ATG-Client-Update-Version";

    private String manifestVersion;
    private final List<String> atgRequired = new ArrayList<String>();
    private final List<String> atgClientClassPath = new ArrayList<String>();
    private final List<String> atgClassPath = new ArrayList<String>();
    private final List<String> atgConfigPath = new ArrayList<String>();
    private Boolean atgClientUpdateFile;
    private String name;
    private String atgClientUpdateVersion;
    private Path path;

    public AtgMetaInf(final Path path) {
        try {
            this.path = path;
            final List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            for (final String line : lines) {
                if (line.trim().startsWith(MANIFEST_VERSION)) {
                    this.manifestVersion = line.substring(MANIFEST_VERSION.length() + 1);
                } else if (line.trim().startsWith(ATG_REQUIRED)) {
                    this.atgRequired.addAll(Arrays.asList(line.substring(ATG_REQUIRED.length() + 1).split(" ")));
                } else if (line.trim().startsWith(ATG_CLIENT_CLASS_PATH)) {
                    this.atgClientClassPath.addAll(Arrays.asList(line.substring(ATG_CLIENT_CLASS_PATH.length() + 1).split(" ")));
                } else if (line.trim().startsWith(ATG_CLASS_PATH)) {
                    this.atgClassPath.addAll(Arrays.asList(line.substring(ATG_CLASS_PATH.length() + 1).split(" ")));
                } else if (line.trim().startsWith(ATG_CONFIG_PATH)) {
                    this.atgConfigPath.addAll(Arrays.asList(line.substring(ATG_CONFIG_PATH.length() + 1).split(" ")));
                } else if (line.trim().startsWith(NAME)) {
                    this.name = line.substring(NAME.length() + 1);
                } else if (line.trim().startsWith(ATG_CLIENT_UPDATE_FILE)) {
                    this.atgClientUpdateFile = Boolean.valueOf(line.substring(ATG_CLIENT_UPDATE_FILE.length() + 1));
                } else if (line.trim().startsWith(ATG_CLIENT_UPDATE_VERSION)) {
                    this.atgClientUpdateVersion = line.substring(ATG_CLIENT_UPDATE_VERSION.length() + 1);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for atgManifestVersion.
     * @return the atgManifestVersion
     */
    public String getAtgManifestVersion() {
        return manifestVersion;
    }

    /**
     * Getter for atgRequired.
     * @return the atgRequired
     */
    public List<String> getAtgRequired() {
        return atgRequired;
    }

    /**
     * Getter for atgClientClassPath.
     * @return the atgClientClassPath
     */
    public List<String> getAtgClientClassPath() {
        return atgClientClassPath;
    }

    /**
     * Getter for atgClassPath.
     * @return the atgClassPath
     */
    public List<String> getAtgClassPath() {
        return atgClassPath;
    }

    /**
     * Getter for atgConfigPath.
     * @return the atgConfigPath
     */
    public List<String> getAtgConfigPath() {
        return atgConfigPath;
    }

    /**
     * Getter for atgClientUpdateFile.
     * @return the atgClientUpdateFile
     */
    public boolean isAtgClientUpdateFile() {
        return atgClientUpdateFile;
    }

    /**
     * Getter for name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for atgClientUpdateVersion.
     * @return the atgClientUpdateVersion
     */
    public String getAtgClientUpdateVersion() {
        return atgClientUpdateVersion;
    }

    /**
     * Getter for path.
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("*** Path").append(": ").append(this.path).append(NEW_LINE).append(NEW_LINE);
        sb.append(MANIFEST_VERSION).append(": ").append(this.manifestVersion).append(NEW_LINE);

        if (this.atgRequired != null && !this.atgRequired.isEmpty()) {
            sb.append(ATG_REQUIRED).append(": ");
            for (final String required : this.atgRequired) {
                sb.append(required).append(" ");
            }
            sb.append(NEW_LINE);
        }
        if (this.atgClientClassPath != null && !this.atgClientClassPath.isEmpty()) {
            sb.append(ATG_CLIENT_CLASS_PATH).append(": ");
            for (final String required : this.atgClientClassPath) {
                sb.append(required).append(" ");
            }
            sb.append(NEW_LINE);
        }
        if (this.atgClassPath != null && !this.atgClassPath.isEmpty()) {
            sb.append(ATG_CLASS_PATH).append(": ");
            for (final String required : this.atgClassPath) {
                sb.append(required).append(" ");
            }
            sb.append(NEW_LINE);
        }
        if (this.atgConfigPath != null && !this.atgConfigPath.isEmpty()) {
            sb.append(ATG_CONFIG_PATH).append(": ");
            for (final String required : this.atgConfigPath) {
                sb.append(required).append(" ");
            }
            sb.append(NEW_LINE);
        }
        sb.append(NEW_LINE);

        if (this.name != null) {
            sb.append(NAME).append(": ").append(this.name).append(NEW_LINE);
        }
        if (this.atgClientUpdateFile != null) {
            sb.append(ATG_CLIENT_UPDATE_FILE).append(": ").append(this.atgClientUpdateFile).append(NEW_LINE);
        }
        if (this.atgClientUpdateVersion != null) {
            sb.append(ATG_CLIENT_UPDATE_VERSION).append(": ").append(this.atgClientUpdateVersion).append(NEW_LINE);
        }
        return sb.toString();
    }

    /**
     * @param atgMetaInf
     * @return
     */
    @Override
    public int compareTo(final AtgMetaInf atgMetaInf) {
        if (this.path != null && atgMetaInf != null && atgMetaInf.getPath() != null) {
            return this.path.toString().compareTo(atgMetaInf.getPath().toString());
        } else {
            return 0;
        }
    }
}
