# DropKey

**DropKey** is an open-source, self-hosted application for receiving files through temporary upload links.

Create a drop, share its public link with someone who needs to send you files, and manage the drop through a separate private admin link. A public drop can optionally be protected by a password.

> **Project status:** Early development. The backend currently supports creating drops, viewing public drop metadata, and verifying passwords to issue short-lived access tokens. **File uploads, downloads, the web interface, and automatic cleanup are not implemented yet.** Do not deploy this version publicly.

## Planned features

- Temporary public links for receiving files
- Optional password protection for upload links
- Private admin links for managing and downloading received files
- Configurable expiration and upload limits
- Automatic cleanup of expired drops and files
- Self-hosting with Docker

## Tech stack

| Component | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 4 |
| Frontend (planned) | Angular |
| Database | MySQL 8.4 LTS |
| Database migrations | Flyway |
| Build | Maven |
| Deployment (planned) | Docker |

## Repository structure

```text
dropkey/
├── core/                 # Spring Boot backend
├── web/                  # Angular frontend (planned)
├── .github/workflows/    # CI workflows
├── compose.yaml          # Local MySQL development environment
└── README.md
```

## Getting started (backend development)

### Requirements

- Java 21
- Maven (or the Maven Wrapper, if included in `core/`)
- Docker with Docker Compose

### 1. Start MySQL

From the repository root:

```bash
docker compose up -d mysql
```

The included Compose configuration uses local development credentials. **Do not use these credentials in production.**

### 2. Start the backend

From `core/`:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080` by default. Flyway applies database migrations at startup.

The default local database connection is `jdbc:mysql://localhost:3306/dropkey`, with username and password `dropkey`. These values are intended only for local development.

### 3. Run tests

From `core/`:

```bash
mvn clean test
```

Current unit tests use JUnit and Mockito and do not require a running database. Tests that start the full Spring application may require MySQL.

## Current API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/drops` | Create a temporary drop |
| `GET` | `/api/drops/{publicToken}` | Read public drop information |
| `POST` | `/api/drops/{publicToken}/verify-password` | Verify a drop password and obtain a short-lived access token |

Example request to create a drop:

```bash
curl -X POST http://localhost:8080/api/drops \
  -H 'Content-Type: application/json' \
  -d '{"expiresInHours":24,"password":"example-password"}'
```

The response contains both a `publicToken` and a private `adminToken`. **Treat tokens as secrets where applicable, especially the admin token.** The admin token is not needed to read public drop information.

## Security status

DropKey is not production-ready. Before public deployment, the project needs upload authorization enforcement, rate limiting, safe file handling, storage limits, and production security configuration. Use HTTPS when deploying a future production-ready release.

Never commit real credentials, private tokens, or local `.env` files.

## Development and contributions

This project is in an early stage. Contributions, bug reports, and suggestions are welcome through GitHub issues and pull requests.

## License

See [LICENSE](LICENSE) for the project's license.
