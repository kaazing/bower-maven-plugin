# Kaazing bower-dependency-maven-plugin

[![Build Status][build-status-image]][build-status]

[build-status-image]: https://travis-ci.org/kaazing/bower-dependency-maven-plugin.svg?branch=develop
[build-status]: https://travis-ci.org/kaazing/bower-dependency-maven-plugin

Plugin to handle bower dependencies in Maven

Usage to unpack dependencies into target/bower-dependencies
```
<plugin>
    <groupId>org.kaazing</groupId>
    <artifactId>unpack-bower-dependency-maven-plugin</artifactId>
    <version>1.0.1</version>
    <executions>
        <execution>
            <goals>
                <goal>unpack</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <bowerDependencies>
            <bowerDependency>
                <!-- name will unpack to target/bower-dependencies/jquery-->
                <name>jquery</name>
                <!-- location of the git url -->
                <location>https://github.com/jquery/jquery</location>
                <!-- Version to get from the tags -->
                <version>2.0.3</version>
            </bowerDependency>
            <bowerDependency>
                <name>command-center</name>
                <!-- location can be bower short hand of owner/repo -->
                <location>kaazing/command-center</location>
                <!-- version ranges can be done using Maven version ranges -->
                <version>[1.0.0.0,2.0.0.0]</version>
            </bowerDependency>
        </bowerDependencies>
    </configuration>
</plugin>
```
