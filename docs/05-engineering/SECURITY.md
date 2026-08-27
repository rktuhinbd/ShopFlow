# ShopFlow — Security

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Scope

ShopFlow is a read-only product catalog app with no authentication, no user accounts, and no sensitive data. Security requirements are proportionate to this scope.

## 2. Requirements

### 2.1 Secret Handling
- **No API keys required** — DummyJSON is a public API
- No hardcoded secrets in source code
- If any secrets are added later: use `local.properties` (gitignored) or BuildConfig fields

### 2.2 Logging
- **Production**: No sensitive data in logs (NFR-501)
- Use `Log.d`/`Log.i` only in debug builds
- Strip logging in release via R8 or conditional compilation
- OkHttp logging interceptor: debug only

### 2.3 Permissions
- **INTERNET** permission only (required for API calls)
- No unnecessary permissions

### 2.4 Network Security
- All API calls via HTTPS (NFR-502)
- Network security config: enforce HTTPS
- Certificate pinning: not required for DummyJSON (public mock API)
- OkHttp timeouts configured to prevent hanging

### 2.5 Dependency Security
- Use only well-known, actively maintained libraries
- Regularly check for dependency vulnerabilities
- Pin dependency versions in Version Catalog

### 2.6 Build Artifacts
- Debug and release build types separated (NFR-503)
- Debug features stripped from release
- ProGuard/R8 obfuscation in release
- `debuggable = false` in release (default)

### 2.7 Data Storage
- Room database: app-private storage (default)
- No external storage usage
- No data exported to other apps
- Backup rules configured (`backup_rules.xml` exists)

---

**Document Status**: DRAFT — Awaiting human review and approval.
