# Repository Guidelines

## Project Structure & Module Organization
- Source: src/main/java/com/hsmy (domain packages: controller/, service/, mapper/, entity/, config/, dto/, o/).
- Resources: src/main/resources (pplication.yml, profile files pplication-*.yml, MyBatis XML in mapper/, Flyway SQL in db/migration/).
- Tests: src/test/java (JUnit; name tests *Test).
- Docs: /docs, /prd (standards), API guides: API_VERSION_GUIDE.md, API_VERSION_PATH_MAPPING.md.

## Build, Test, and Development Commands
- Build (dev profile): mvn clean package -Pdev
- Build (test/prd): mvn clean package -Ptest or -Pprd
- Run locally (dev): mvn spring-boot:run
- Run JAR: java -jar target/HSMY-1.0-SNAPSHOT.jar --spring.profiles.active=dev
- Tests: mvn test (run all) or mvn -Dtest=ClassNameTest test (single test)

## Coding Style & Naming Conventions
- Java 8; 4-space indent; UTF-8; use Lombok for boilerplate (@Data, @Builder).
- Packages by layer: controller -> service -> mapper -> entity.
- Naming: XxxController, XxxService, XxxMapper, XxxEntity; DTO/VO end with Request/Response/VO.
- SQL mappers live in src/main/resources/mapper/*.xml matching com.hsmy.mapper.XxxMapper.

## Testing Guidelines
- Use JUnit (spring-boot-starter-test). Prefer @SpringBootTest for integration, mock external IO.
- Name tests *Test.java; keep tests under matching package paths.
- Run with profiles when needed: mvn -Ptest test.

## Commit & Pull Request Guidelines
- Commit style (from history): [feat] ..., [fix] ..., [refactor] .... Keep subject <= 72 chars; imperative mood.
- PRs: include description, linked issue (e.g., Closes #123), screenshots for API/UI changes, and test notes.
- Small, focused PRs; ensure mvn -Pdev clean verify passes.

## Security & Configuration Tips
- Do not commit secrets. Configure via env vars used in pplication.yml (e.g., EMAIL_*, ALIYUN_*, TENCENT_*).
- Database/Redis config per profile in pplication-*.yml. Default HTTP base path: /api on port 8080.
- Static uploads default to ./uploads (/api/file/uploads/**).