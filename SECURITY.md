# Security Policy

ShopFlow prioritizes the security of user data and the integrity of the application. While this is a frontend-heavy application, standard security practices apply.

## Reporting a Vulnerability

If you discover a security vulnerability in the ShopFlow Android application, please do not disclose it publicly.

Send an email to the security team or the repository maintainer directly. We will attempt to acknowledge your report within 48 hours and provide a timeline for a fix.

## Secrets Handling

- **Never commit credentials**: API keys, authentication tokens, signing keys, and passwords MUST NOT be committed to the repository.
- Use `local.properties` or environment variables in CI/CD pipelines to inject sensitive configuration values during the build process.

## Dependency Hygiene

- We regularly audit our third-party dependencies (Retrofit, Room, OkHttp, Coil, etc.) for known vulnerabilities.
- Dependencies are locked to specific versions in `gradle/libs.versions.toml`. Do not use dynamic versioning (e.g., `1.2.+`).

## Network Security

- The application uses HTTPS for all remote API communication via Retrofit and OkHttp.
- Cleartext traffic is disabled by default on modern Android versions and is not overridden in our network configuration.

*(Note: Currently, ShopFlow relies on the DummyJSON API for demonstration purposes. In a true production deployment, authentication and token refresh logic would be integrated into the OkHttp interceptor chain.)*
