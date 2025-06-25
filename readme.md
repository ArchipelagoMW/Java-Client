Archipelago Java Client
=======================
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.archipelagomw/Java-Client)](https://central.sonatype.com/artifact/io.github.archipelagomw/Java-Client)

A java client Library to connect to an [Archipelago](http://github.com/ArchipelagoMW/Archipelago) Server.


## Getting Started
Use the following Code snippits to add this library to your project using the following.

### Maven
To use maven add this dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>io.github.archipelagomw</groupId>
    <artifactId>Java-Client</artifactId>
    <version>0.1.20</version>
</dependency>
```

### Gradle
To use Gradle add the maven central repository to your repositories list:
then add this to your `dependancy` section
```groovy
implementation 'io.github.archipelagomw:Java-Client:0.1.20'
```

## Using Snapshots
This repository is setup to publish snapshots when new commits hit `main`.  If you want
to use the snapshot version you will need to do the following:

### Maven
From [Maven Central Documentation](https://central.sonatype.org/publish/publish-portal-snapshots/#publishing-via-other-methods)

Configure your pom.xml file with the following <repositories> section:

```xml
<repositories>
    <repository>
        <name>Central Portal Snapshots</name>
        <id>central-portal-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

And add the snapshot version into your dependencies:

```xml
<dependency>
    <groupId>io.github.archipelagomw</groupId>
    <artifactId>Java-Client</artifactId>
    <version>0.1.20-SNAPSHOT</version>
</dependency>
```


### Gradle

From [Maven Central Documentation](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle)

Configure your `build.gradle` with the following:

```groovy
repositories {
  maven {
    name = 'Central Portal Snapshots'
    url = 'https://central.sonatype.com/repository/maven-snapshots/'

    // Only search this repository for the specific dependency
    content {
      includeModule("io.github.archipelagomw", "Java-Client")
    }
  }
  mavenCentral()
}
```

And add the snapshot version into your dependencies:
```groovy
implementation 'io.github.archipelagomw:Java-Client:0.1.20-SNAPSHOT'
```
