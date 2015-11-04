# bower-maven-plugin

[![Build Status][build-status-image]][build-status]

[build-status-image]: https://travis-ci.org/kaazing/bower-maven-plugin.svg?branch=develop
[build-status]: https://travis-ci.org/kaazing/bower-maven-plugin

Plugin to handle bower dependencies in Maven, (1st example) or to upload bower artifacts (2nd example)

Usage to unpack dependencies into target/bower-dependencies
```xml
<plugin>
    <groupId>org.kaazing</groupId>
    <artifactId>bower-maven-plugin</artifactId>
    <version>2.0.0</version>
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

Usage to upload artifacts
```xml
<plugin>
    <groupId>org.kaazing</groupId>
    <artifactId>bower-maven-plugin</artifactId>
    <version>2.0.0</version>

    <executions>
        <execution>
            <id>deploy</id>
            <phase>install</phase>
            <goals>
                <goal>upload</goal>
            </goals>
            <configuration>
                <gitBowerUrl>https://github.com/kaazing/gateway-javascript</gitBowerUrl>
                <includeBaseDir>${project.build.directory}/verify/lib</includeBaseDir>
                <password>${password}</password>
                <username>${username}</username>
                <includes>
                    <include>Loader.swf</include>
                    <include>PostMessage.js</include>
                    <include>ServerSentEvents-debug.js</include>
                    <include>WebSocket-debug.js</include>
                    <include>XMLHttpRequest-debug.js</include>
                    <include>PostMessage-debug.js</include>
                    <include>PostMessageBridge.html</include>
                    <include>ServerSentEvents.js</include>
                    <include>WebSocket.js</include>
                    <include>XMLHttpRequest.js</include>
                </includes>
            </configuration>
        </execution>
    </executions>
</plugin>
```
