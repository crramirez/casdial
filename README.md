# Casdial

Dialog command compatible based on Casciian TUI library

## Description

Casdial is a text-based dialog utility built using the [Casciian](https://github.com/crramirez/casciian) Java TUI library. This is a simple "Hello World" demonstration application.

## Prerequisites

- Java 21 or later
- Gradle 9.2.1 or later (included via wrapper)
- For native image compilation: GraalVM Java 25 with native-image
- For packaging: fpm (installed via `gem install fpm`)

## Building

### Standard JAR Build

```bash
./gradlew clean build
```

This creates a JAR file in `build/libs/casdial-<version>.jar`

### Running the Application

```bash
./gradlew installDist
./build/install/casdial/bin/casdial
```

Or with Java directly:

```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
java -jar build/libs/casdial-<version>.jar
```

### Native Image Compilation (Required for Packaging)

The DEB and RPM packages require a native binary. You need GraalVM Java 25 with native-image installed.

#### Installing GraalVM

You can install GraalVM using SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25.0.0.r25-graalce
```

Or download directly from [GraalVM Downloads](https://www.graalvm.org/downloads/).

#### Building Native Binary

1. Ensure GraalVM Java 25 is installed and configured
2. Run:

```bash
./gradlew nativeCompile
```

This creates a native executable at `build/native/nativeCompile/casdial`

### Creating DEB and RPM Packages

**Important:** The packages require a native binary. You must first compile the native binary using GraalVM (see above).

#### Prerequisites for Packaging

```bash
# Install fpm and dependencies
sudo apt-get install ruby ruby-dev build-essential rpm
sudo gem install fpm
```

#### Building Packages

After compiling the native binary, build both DEB and RPM packages:

```bash
./gradlew buildPackages
```

Or build individually:

```bash
./gradlew buildDeb    # Creates DEB package in build/distributions/deb/
./gradlew buildRpm    # Creates RPM package in build/distributions/rpm/
```

The packages will include only:
- `/usr/bin/casdial` - Native executable binary

#### Installing the Packages

**Debian/Ubuntu:**
```bash
sudo dpkg -i build/distributions/deb/casdial_0.1.0-1_amd64.deb
sudo apt-get install -f  # Install dependencies if needed
```

**RedHat/CentOS/Fedora:**
```bash
sudo rpm -ivh build/distributions/rpm/casdial-0.1.0-1.x86_64.rpm
```

After installation, you can run the application:
```bash
casdial
```

## Project Structure

```
casdial/
├── build.gradle              # Gradle build configuration
├── settings.gradle           # Gradle settings
├── gradle.properties         # Project version and properties
├── src/
│   └── main/
│       └── java/
│           └── io/github/crramirez/casdial/
│               └── HelloWorld.java   # Main application
└── README.md
```

## License

Apache License 2.0 - Copyright 2025 Carlos Rafael Ramirez

## Dependencies

- [Casciian 1.1](https://github.com/crramirez/casciian) - Java Text User Interface library

