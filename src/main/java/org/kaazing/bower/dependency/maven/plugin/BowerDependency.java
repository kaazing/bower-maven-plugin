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
