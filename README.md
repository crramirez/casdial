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

1. Install GraalVM Java 25 with native-image support
2. Edit `build.gradle` and uncomment the `javaLauncher` section in the `graalvmNative` block
3. Run:

```bash
./gradlew nativeCompile
```

This creates a native executable at `build/native/nativeCompile/casdial`

#### Building Native Packages

To build packages with the native binary:

```bash
./gradlew buildNativePackages
```

This will first compile the native image, then create both DEB and RPM packages with the native binary.

### Creating DEB and RPM Packages

The project supports creating both DEB and RPM packages for Linux. The packages can be built from either the native binary (if available) or from the JAR file with a wrapper script.

#### Prerequisites for Packaging

```bash
# Install fpm and dependencies
sudo apt-get install ruby ruby-dev build-essential rpm
sudo gem install fpm
```

#### Building Packages

Build both DEB and RPM packages:

```bash
./gradlew buildPackages
```

Or build individually:

```bash
./gradlew buildDeb    # Creates DEB package in build/distributions/deb/
./gradlew buildRpm    # Creates RPM package in build/distributions/rpm/
```

The packages will include:
- `/usr/bin/casdial` - Executable wrapper script (or native binary if built with GraalVM)
- `/usr/share/casdial/casdial-<version>.jar` - Application JAR (when not using native image)

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

MIT License - Copyright 2025 Carlos Rafael Ramirez

## Dependencies

- [Casciian 0.7](https://github.com/crramirez/casciian) - Java Text User Interface library

