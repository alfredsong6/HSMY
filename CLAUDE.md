# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Maven-based Java project with standard directory structure:
- Main application: `src/main/java/com/hsmy/App.java`
- Tests: `src/test/java/com/hsmy/AppTest.java`
- Build configuration: `pom.xml`

## Maven Configuration
- Group ID: `com.hsmy`
- Artifact ID: `HSMY`
- Version: `1.0-SNAPSHOT`
- Java project with JUnit 3.8.1 for testing

## Common Commands
Build and compile:
```bash
mvn clean compile
```

Run tests:
```bash
mvn test
```

Clean build artifacts:
```bash
mvn clean
```

Package application:
```bash
mvn package
```

Run application:
```bash
mvn exec:java -Dexec.mainClass="com.hsmy.App"
```

## Architecture
This is a basic Maven Java application with minimal structure:
- Single main class `App.java` with a simple "Hello World" implementation
- Basic JUnit test setup in `AppTest.java`
- Standard Maven project layout following convention over configuration
- Uses custom Maven repository at `http://10.10.0.58:8081/repository/ftxy-group/`

## Development Notes
- Follow standard Java naming conventions
- Tests use JUnit 3.x framework
- Target and build artifacts are created in `target/` directory