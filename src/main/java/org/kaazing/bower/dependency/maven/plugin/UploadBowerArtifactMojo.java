/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.bower.dependency.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Upload Bower Artifact
 * 
 * @goal upload
 * @phase deploy
 */
public class UploadBowerArtifactMojo extends AbstractMojo {

    /**
     * @parameter
     * default-value = "${project}"
     */
    public MavenProject project;

    /**
     * @parameter
     * default-value = "${project.version}"
     */
    public String version;

    /**
     * @parameter
     */
    public String[] includes;

    /**
     * @parameter
     */
    public String directoryToInclude;

    /**
     * @parameter
     * default-value = "${project.build.directory}
     */
    public String includeBaseDir;

    /**
     * @parameter
     */
    public String gitBowerUrl;

    /**
     * @parameter
     * default-value = "${project.build.directory}/bower-upload")
     */
    public String outputDir;

    /**
     * @parameter
     */
    public String username;

    /**
     * @parameter
     */
    public String password;

    /**
     * Preserve files in repo, if null or emtpy defaults to:
     *    README.md, bower.json, package.json, and .git
     * Note: http://stackoverflow.com/questions/1659087/how-to-configure-defaults-for-a-parameter-with-multiple-values-for-a-maven-plugin
     * If you want to delete all just add a dummy file to the list in the config
     * @parameter
     */
    public List<String> preserveFiles;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
        File repoDir = new File(outputDir);
        Git repo = checkoutGitRepo(repoDir, gitBowerUrl, credentialsProvider);
        if (preserveFiles == null || preserveFiles.isEmpty()) {
            preserveFiles = new ArrayList<String>();
            preserveFiles.add("README.md");
            preserveFiles.add("bower.json");
            preserveFiles.add("package.json");
            preserveFiles.add(".git");

        }
        for (File file : repoDir.listFiles()) {
            if (!preserveFiles.contains(file.getName())) {
                file.delete();
                try {
                    repo.rm().addFilepattern(file.getName()).call();
                } catch (GitAPIException e) {
                    throw new MojoExecutionException("Failed to reset repo", e);
                }
            }
        }

        for (String include : includes) {
            File includedFile = new File(includeBaseDir, include);
            if (!includedFile.exists()) {
                throw new MojoExecutionException("Included file \"" + include + "\" does not exist at includeBaseDir/{name}: "
                        + includedFile.getAbsolutePath());
            }
            if (includedFile.isDirectory()) {
                throw new MojoExecutionException("Included files can not be directory: " + includedFile);
            }
            try {
                Files.copy(includedFile.toPath(), new File(outputDir, include).toPath());
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy included resource", e);
            }
            try {
                repo.add().addFilepattern(include).call();
            } catch (GitAPIException e) {
                throw new MojoExecutionException("Failed to add included file", e);
            }
        }

        if (directoryToInclude != null && !directoryToInclude.equals("")) {
            File includedDir = new File(directoryToInclude);
            if (!includedDir.exists() && includedDir.isDirectory()) {
                throw new MojoExecutionException("Included directory \"" + directoryToInclude + "\" does not exist");
            }
            for (File includedFile : includedDir.listFiles()) {
                String include = includedFile.getName();
                if (includedFile.isDirectory()) {
                    throw new MojoExecutionException("Included files can not be directory: " + includedFile);
                }
                try {
                    Files.copy(includedFile.toPath(), new File(outputDir, include).toPath());
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to copy included resource", e);
                }
                try {
                    repo.add().addFilepattern(include).call();
                } catch (GitAPIException e) {
                    throw new MojoExecutionException("Failed to add included file", e);
                }
            }
        }

        try {
            repo.commit().setMessage("Added files for next release of " + version).call();
        } catch (GitAPIException e) {
            throw new MojoExecutionException("Failed to commit to repo with changes", e);
        }
        try {
            repo.tag().setName(version).setMessage("Releasing version: " + version).call();
        } catch (GitAPIException e) {
            throw new MojoExecutionException("Failed to tag release", e);
        }
        try {
            repo.push().setPushTags().setCredentialsProvider(credentialsProvider).call();
            repo.push().setPushAll().setCredentialsProvider(credentialsProvider).call();
        } catch (GitAPIException e) {
            throw new MojoExecutionException("Failed to push changes", e);
        }
    }

    /**
     * Checkouts a git repo
     * @param outputDir
     * @param gitUrl
     * @param credentialsProvider 
     * @return
     * @throws MojoExecutionException
     */
    Git checkoutGitRepo(File outputDir, String gitUrl, CredentialsProvider credentialsProvider) throws MojoExecutionException {
        outputDir.mkdir();
        try {
            return Git.cloneRepository().setURI(gitUrl).setDirectory(outputDir).setCredentialsProvider(credentialsProvider)
                    .call();
        } catch (Exception e) {
            throw new MojoExecutionException("Could not fetch git repository", e);
        }
    }
}
