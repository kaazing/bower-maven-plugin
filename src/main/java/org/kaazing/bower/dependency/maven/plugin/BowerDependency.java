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

/**
 * BowerDependency is
 * 
 */
public class BowerDependency {

    /**
     * Name of the dependency
     * @required
     * @parameter
     */
    private String name;

    /**
     * Version must follow the semantic version spec or Maven version ranges
     * @required
     * @parameter
     */
    private String version;

    /**
     * location must be a valid version, a Git URL, or a URL (inc. tarball, file, and zipball), or location can be an
     * owner/package shorthand, i.e. owner/package. By default, the shorthand resolves to GitHub ->
     * https://github.com/owner/package.
     * @required
     * 
     * If it is not listed it will pull it from bower central, as of now this is not implemented
     */
    private String location;

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    /**
     * For Testing
     * @param name
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * For Testing
     * @param version
     */
    void setVersion(String version) {
        this.version = version;
    }

    void setLocation(String location) {
        this.location = location;
    }

}
