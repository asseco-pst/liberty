## Liberty

[![CircleCI](https://circleci.com/gh/asseco-pst/liberty/tree/develop.svg?style=svg)](https://circleci.com/gh/asseco-pst/liberty/tree/develop)
[![CodeFactor](https://www.codefactor.io/repository/github/asseco-pst/liberty/badge)](https://www.codefactor.io/repository/github/asseco-pst/liberty)

### What is it?
Liberty is an open source tool to allow deploying artifacts into Open Liberty or IBM Websphere Liberty servers. It provides a deploy builder that will assist in the necessary
configuration for the deployer service. Using the deployer service, the following actions are currently supported:
 - install artifact
 - uninstall artifact
 - start artifact
 - stop artifact
 - restart artifact
 - get a list of installed artifacts
 - get a list of installed artifacts for a given package
 - change the autostart configuration (only supported for the custom strategy)

An artifact is a java package (war, ear, etc) that is currently supported by Open Liberty or IBM Websphere Liberty.

Right now, Liberty is supplied only as an importable dependency, but a CLI version is set to be developed as part of the roadmap.

### Getting Started

Import using Maven or Gradle:

```xml
<dependency>
    <groupId>io.github.asseco-pst</groupId>
    <artifactId>liberty</artifactId>
    <version>...</version>
</dependency>
```

```groovy
compile group: 'io.github.asseco-pst', name: 'liberty', version: '...'
```

### Build from source
1. Clone the project
```sh
git clone git@github.com:asseco-pst/liberty.git
```

2. Run the following command on the root of the project:
```sh
gradlew build
```

## Usage

### As a CLI
#### Running the executable

To be developed as part of the roadmap.

### As a Groovy Lib

Install a new artifact in a Liberty Server
```groovy
// Sets up the deployment service 
IDeployService deployService = deployBuilder
                    .setProfileDetails('<server_uri>>', '<server_port>', '<username>', '<password>')
                    .setDeployStrategy(Strategy.DROPINS)
                    .setArtifactPath("<path_to_artifact>")
                    .build()

// Installs and starts the artifact on the Liberty server
 deployService.connect().installArtifact().await(2000).startArtifact()
```
