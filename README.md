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

### Native Image Compilation (Optional)

If you have GraalVM Java 25 installed with native-image:

1. Edit `build.gradle` and uncomment the `javaLauncher` section in the `graalvmNative` block
2. Run:

```bash
./gradlew nativeCompile
```

This creates a native executable at `build/native/nativeCompile/casdial`

### Creating DEB and RPM Packages

After building the native image:

```bash
# Install fpm if not already installed
sudo apt-get install ruby ruby-dev build-essential rpm
sudo gem install fpm

# Build packages
./gradlew buildPackages
```

Or build individually:

```bash
./gradlew buildDeb    # Creates DEB package in build/distributions/deb/
./gradlew buildRpm    # Creates RPM package in build/distributions/rpm/
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

MIT License - Copyright 2025 Carlos Rafael Ramirez

## Dependencies

- [Casciian 0.7](https://github.com/crramirez/casciian) - Java Text User Interface library

