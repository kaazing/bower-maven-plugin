# Kaazing bower-dependency-maven-plugin

Plugin to handle bower dependencies in Maven

Usage
```
<plugin>
    <groupId>org.kaazing</groupId>
    <artifactId>dependency-bower-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
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
                <name>jquery</name>
                <location>https://github.com/jquery/jquery</location>
                <version>2.0.3</version>
            </bowerDependency>
            <bowerDependency>
                <name>command-center</name>
                <location>https://github.com/kaazing/command-center</location>
                <version>[1.0.0.0,2.0.0.0]</version>
            </bowerDependency>
        </bowerDependencies>
    </configuration>
</plugin>
```
