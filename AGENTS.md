# Repository Guidelines

## Project Structure & Module Organization
Source code lives under `src/main/java/com/hsmy`, grouped by layer (`controller`, `service`, `mapper`, `entity`, `config`, `dto`, `vo`). Spring resources and MyBatis XML reside in `src/main/resources`; Flyway migrations belong in `src/main/resources/db/migration`. Keep integration utilities and profile configs in the relevant `application-*.yml` files. Tests mirror the main package layout in `src/test/java`, and documentation belongs in `/docs` or `/prd`.

## Build, Test, and Development Commands
Use `mvn clean package -Pdev` for a full development build, or switch to `-Ptest` and `-Pprd` for environment-specific artifacts. Run the service locally with `mvn spring-boot:run` (defaults to the `dev` profile) or execute the packaged JAR via `java -jar target/HSMY-1.0-SNAPSHOT.jar --spring.profiles.active=dev`. Execute all tests with `mvn test`, and narrow to a single class using `mvn -Dtest=ClassNameTest test`.

## Coding Style & Naming Conventions
Write Java 8 code with 4-space indentation and UTF-8 encoding. Leverage Lombok annotations (e.g., `@Data`, `@Builder`) for boilerplate. Name components by layer: `XxxController`, `XxxService`, `XxxMapper`, `XxxEntity`. DTOs and view objects end with `Request`, `Response`, or `VO`. Align MyBatis mappers under `src/main/resources/mapper` with matching `com.hsmy.mapper` interfaces.

## Testing Guidelines
Prefer `@SpringBootTest` for integration tests and isolate external IO with mocks. Name test classes `*Test.java` and place them under the mirrored package of the component under test. Run targeted suites using Maven profiles when the test data depends on `application-test.yml`.

## Commit & Pull Request Guidelines
Follow the existing commit format such as `[feat] Add user onboarding API` or `[fix] Resolve null pointer on login`, keeping subjects under 72 characters and in imperative mood. Pull requests should include a clear summary, reference related issues (`Closes #123`), attach API screenshots when endpoints change, and document test evidence (`mvn -Pdev clean verify`).

## Security & Configuration Tips
Do not commit secrets; instead, pass credentials via environment variables consumed in `application.yml` (e.g., `EMAIL_*`, `ALIYUN_*`, `TENCENT_*`). Respect the default REST base path `/api` on port 8080, and note that user-uploaded assets default to `./uploads` served from `/api/file/uploads/**`.
