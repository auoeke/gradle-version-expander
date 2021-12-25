Don't use this plugin; [Gradle does its job already](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:resolved_dependencies).

Dependency constraint versions are not always expanded.

## examples
### POM
```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>[4,)</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>net.auoeke</groupId>
        <artifactId>wheel</artifactId>
        <version>+</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```
is replaced by
```xml
<dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>5.8.2</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
      <version>2.8.7</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>net.auoeke</groupId>
      <artifactId>wheel</artifactId>
      <version>0-SNAPSHOT</version>
      <scope>runtime</scope>
    </dependency>
</dependencies>
```
.
### Gradle metadata
```json
        {
          "group": "com.google.code.gson",
          "module": "gson"
        }
```
```json
        {
          "group": "net.auoeke",
          "module": "wheel",
          "version": {
            "requires": "+"
          }
        }
```
```json
        {
          "group": "org.junit.jupiter",
          "module": "junit-jupiter",
          "version": {
            "requires": "[4,)"
          }
        }
```
is replaced by
```json
        {
          "group": "com.google.code.gson",
          "module": "gson",
          "version": {
            "requires": "2.8.7"
          }
        }
```
```json
        {
          "group": "net.auoeke",
          "module": "wheel",
          "version": {
            "requires": "0-SNAPSHOT"
          }
        }
```
```json
        {
          "group": "org.junit.jupiter",
          "module": "junit-jupiter",
          "version": {
            "requires": "5.8.2"
          }
        }
```
.
