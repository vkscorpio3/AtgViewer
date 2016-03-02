package com.douglas.atg.project;

/*
 * Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@code FileVisitor} that finds
 * all files that match the
 * specified pattern.
 */
public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher fileMatcher;
    private final List<PathMatcher> dirMatchers;
    private final List<Path> paths = new ArrayList<>();

    Finder(final String filePattern, final String... dirPatterns) {
        dirMatchers = new ArrayList<PathMatcher>();
        for (final String dirPattern : dirPatterns) {
            dirMatchers.add(FileSystems.getDefault().getPathMatcher("glob:" + dirPattern));
        }
        fileMatcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
    }

    public List<Path> getMetaFiles() {
        return paths;
    }

    // Compares the glob pattern against
    // the file or directory name.
    void findFiles(final Path file) {
        final Path name = file.getFileName();
        if (name != null && fileMatcher.matches(name)) {
            paths.add(file);
        }
    }

    // Compares the glob pattern against
    // the file or directory name.
    boolean findDir(final Path dir) {
        if (dir != null) {
            for (final PathMatcher dirMatcher : dirMatchers) {
                if (dirMatcher.matches(dir)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
        if (!Files.isDirectory(file)) {
            findFiles(file);
        }
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
        if (findDir(dir)) {
            findFiles(dir);
            return CONTINUE;
        } else {
            return SKIP_SUBTREE;
        }
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}
