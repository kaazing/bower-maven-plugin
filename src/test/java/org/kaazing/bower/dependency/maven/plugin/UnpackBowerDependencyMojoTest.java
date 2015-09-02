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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;

public class UnpackBowerDependencyMojoTest {

    @Test
    public void testVersionRangePattern() {
        String requiredVersion = "[1.0.0,2.0.0]";
        Matcher matcher = UnpackBowerDependencyMojo.SEMVER_SIMPLE_VERSION_MATCHER.matcher(requiredVersion);
        boolean isRange = !matcher.matches();
        Assert.assertTrue(isRange);
        requiredVersion = "1.0.0";
        Matcher matcher2 = UnpackBowerDependencyMojo.SEMVER_SIMPLE_VERSION_MATCHER.matcher(requiredVersion);
        boolean isRange2 = !matcher2.matches();
        Assert.assertFalse(isRange2);
        requiredVersion = "1.0.0-SNAPSHOT";
        Matcher matcher3 = UnpackBowerDependencyMojo.SEMVER_SIMPLE_VERSION_MATCHER.matcher(requiredVersion);
        boolean isRange3 = !matcher3.matches();
        Assert.assertFalse(isRange3);
    }

    @Test
    public void testShorthandPattern() {
        String shorthand = "kaazing/repoName";
        Matcher matcher = UnpackBowerDependencyMojo.SHORTHAND_PATTERN.matcher(shorthand);
        boolean isShorthand = matcher.matches();
        Assert.assertTrue(isShorthand);
        Assert.assertEquals("kaazing", matcher.group("owner"));
        Assert.assertEquals("repoName", matcher.group("package"));
    }

    @Test
    public void pullInPublicRepo() throws MojoExecutionException {
        UnpackBowerDependencyMojo testMojo = new UnpackBowerDependencyMojo();
        File outputDir = new File("target/test-output/UnpackBowerDependencyMojoTest#pullInPublicRepo");
        outputDir.mkdirs();
        testMojo.setOutputDir(outputDir);
        List<BowerDependency> dependencies = new ArrayList<>();
        BowerDependency bowerDependency = new BowerDependency();
        bowerDependency.setName("kaazing-client-javascript-bridge");
        bowerDependency.setVersion("[5.0.0,5.1.0]");
        bowerDependency.setLocation("https://github.com/kaazing/bower-kaazing-client-javascript-bridge.git");
        dependencies.add(bowerDependency);
        testMojo.setBowerDependencies(dependencies);
        testMojo.execute();
    }
}
