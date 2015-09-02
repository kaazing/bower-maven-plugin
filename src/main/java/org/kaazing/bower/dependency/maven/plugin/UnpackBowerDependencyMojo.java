/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.bower.dependency.maven.plugin;

import static java.util.regex.Pattern.compile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import com.github.zafarkhaja.semver.UnexpectedCharacterException;
import com.github.zafarkhaja.semver.Version;

/**
 * Unpack bower dependencies
 * 
 * @goal unpack
 * @phase generate-resources
 */
public class UnpackBowerDependencyMojo extends AbstractMojo {

    /**
     * Bower Dependency
     * @required
     * @parameter
     */
    private List<BowerDependency> bowerDependencies;

    /**
     * Output Directory
     * @parameter default-value="${project.build.directory}/bower-dependency"
     */
    private File outputDir;

    static final Pattern SEMVER_SIMPLE_VERSION_MATCHER = compile("\\d+\\.\\d+\\.\\d+(-.*)?");
    static final Pattern SHORTHAND_PATTERN = compile("(?<owner>[^/]+)\\/(?<package>[^/]+)");
    Log log = getLog();

    public void execute() throws MojoExecutionException {

        if (getOutputDir().exists()) {
            deleteFully(getOutputDir());
        }
        getOutputDir().mkdirs();

        for (BowerDependency bowerDependency : bowerDependencies) {

            String name = bowerDependency.getName();
            String location = bowerDependency.getLocation();
            String requiredVersion = bowerDependency.getVersion();

            location = parseGitLocation(location);
            log.debug("Git Repo is at " + location);

            Git repo = checkoutGitRepo(new File(getOutputDir(), name), location);

            List<Ref> tagList;
            try {
                tagList = repo.tagList().call();
            } catch (GitAPIException e) {
                throw new MojoExecutionException("Could not tags on repo", e);
            }

            String tag = findMatchingTag(requiredVersion, tagList);

            try {
                repo.checkout().setName(tag).call();
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to switch to tag: " + tag, e);
            }
        }
    }

    /**
     * Takes user inputed location, and returns git location
     * @param location
     * @return
     */
    String parseGitLocation(String location) {
        Matcher locationMatcher = SHORTHAND_PATTERN.matcher(location);
        boolean isShortHand = locationMatcher.matches();
        if (isShortHand) {
            location =
                    String.format("https://github.com/%s/%s", locationMatcher.group("owner"), locationMatcher.group("package"));
        }
        return location;
    }

    /**
     * Checkouts a git repo
     * @param outputDir
     * @param gitUrl
     * @return
     * @throws MojoExecutionException
     */
    Git checkoutGitRepo(File outputDir, String gitUrl) throws MojoExecutionException {
        outputDir.mkdir();
        try {
            return Git.cloneRepository().setURI(gitUrl).setDirectory(outputDir).call();
        } catch (Exception e) {
            throw new MojoExecutionException("Could not fetch git repository", e);
        }
    }

    /**
     * Finds matching tag for a requiredVersion
     * @param requiredVersion (Can be semver version or version range)
     * @param tagList
     * @return
     * @throws MojoExecutionException
     */
    String findMatchingTag(String requiredVersion, List<Ref> tagList) throws MojoExecutionException {
        String tagPrefix = "refs/tags/";
        List<ArtifactVersion> availableVersions = new ArrayList<>();
        for (Ref tag : tagList) {
            String tagVersion = tag.getName().toString().replace(tagPrefix, "");
            log.debug("Found tag version \"" + tagVersion + "\" from tag with name \"" + tag.getName() + "\"");
            try {
                // Check that it follows SEMVER
                Version.valueOf(tagVersion);
                // If it does add it to available versions
                availableVersions.add(new DefaultArtifactVersion(tagVersion));
            } catch (UnexpectedCharacterException e) {
                log.warn("Found tag version \"" + tagVersion + "\" from tag with name \"" + tag.getName()
                        + "\" that does not match semver spec");
            }
        }
        Collections.sort(availableVersions);

        Matcher matcher = SEMVER_SIMPLE_VERSION_MATCHER.matcher(requiredVersion);
        boolean isRange = !matcher.matches();

        String tag = null;

        if (isRange) {
            log.info("version is a range");
            VersionRange versionRange;
            try {
                versionRange = VersionRange.createFromVersionSpec(requiredVersion);
            } catch (InvalidVersionSpecificationException e) {
                throw new MojoExecutionException("Unable to parse version range " + requiredVersion, e);
            }
            for (ArtifactVersion availableVersion : availableVersions) {
                if (versionRange.containsVersion(availableVersion)) {
                    tag = availableVersion.toString();
                }
            }
        } else {
            log.info("version is not a range");
            for (ArtifactVersion availableVersion : availableVersions) {
                log.info(availableVersion.toString() + " compared to " + requiredVersion.toString());
                if (requiredVersion.equals(availableVersion.toString())) {
                    log.info("found tag! " + availableVersion.toString());
                    tag = availableVersion.toString();
                }
            }
        }

        if (tag == null) {
            StringBuilder messageBuilder = new StringBuilder("Could not find a version to match: ");
            messageBuilder.append(requiredVersion);
            messageBuilder.append(", available versions are:");
            for (ArtifactVersion availableVersion : availableVersions) {
                messageBuilder.append("\t");
                messageBuilder.append(availableVersion);
                messageBuilder.append(",");
            }
            throw new MojoExecutionException(messageBuilder.toString());
        }
        tag = "tags/" + tag;
        return tag;
    }

    public void deleteFully(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteFully(child);
            }
        }
        file.delete();
    }

    /**
     * For Testing
     * @return
     */
    void setBowerDependencies(List<BowerDependency> bowerDependencies) {
        this.bowerDependencies = bowerDependencies;
    }

    private File getOutputDir() {
        return outputDir;
    }

    /**
     * For Testing
     * @param outputDir
     */
    void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }
}
