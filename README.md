# Brightloop Subscription Management

Brightloop is a full-stack subscription management app. It includes a React/TypeScript admin workspace and customer portal, plus a Spring Boot API backed by MySQL 8 in the `mysql` profile. Local development defaults to an in-memory H2 database so the app can be explored without installing MySQL.

## Run locally

Requirements: Java 21, Maven 3.9+, Node.js 20+. (The backend targets Java 21.)

Terminal 1:

```sh
cd backend
mvn spring-boot:run
```

Terminal 2:

```sh
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. Vite proxies `/api` to port 8080. In development, Flyway migrates H2 and seeds the demo data. H2 console: http://localhost:8080/dev/h2-console (JDBC URL `jdbc:h2:mem:brightloop`, user `sa`, no password).

### Demo users

| Role | Email | Password |
| --- | --- | --- |
| Admin | admin@brightloop.com | Admin@12345 |
| Staff | staff@brightloop.com | Staff@12345 |
| Customer | customer@brightloop.com | Customer@12345 |

Development credentials only. Change them before any deployment.

## Run with MySQL 8 and Docker

Docker Compose is included. Copy `.env.example` to `.env`, replace all passwords and the JWT secret, then run:

```sh
docker compose up --build
```

Open http://localhost:3000. The MySQL data is kept in the `brightloop-mysql` volume. Compose runs Flyway against MySQL before serving API requests.

## Run with an existing MySQL server

Create the `brightloop` database and application user. Then set the environment variables below and start the backend:

```sh
export SPRING_PROFILES_ACTIVE=mysql
export DATABASE_URL='jdbc:mysql://localhost:3306/brightloop?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DATABASE_USERNAME=brightloop
export DATABASE_PASSWORD='your-database-password'
export JWT_SECRET='a-random-secret-at-least-32-characters-long'
cd backend && mvn spring-boot:run
```

The `mysql` profile uses `ddl-auto=validate`; all schema changes are Flyway migrations in `backend/src/main/resources/db/migration`. Development-only demo accounts are seeded only when the `dev` profile is active. MySQL mode does not create demo accounts.

## Tests and build checks

```sh
cd backend && mvn test
cd ../frontend && npm run build
```

Backend integration tests run against a migrated H2 database and cover seeded role logins, admin/staff/customer authorization boundaries, customer portal ownership, one-time password reset, demo seed counts, duplicate recurring invoice prevention, payment balance checks, quotation PDFs, and invoice cancellation. The API also has a runnable H2 development profile for manual workflows.

## SMTP and password reset

Set `SMTP_ENABLED=true`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, and `SMTP_PASSWORD` to send email. `SMTP_AUTH` and `SMTP_STARTTLS` control SMTP authentication and TLS. When SMTP is disabled, delivery is recorded as a development fallback in the notifications table and logged. Only the `dev` profile exposes the reset token in the API response to make password reset testable locally. Production profiles keep it hidden.

## Implemented API areas

- Authentication: login, customer registration, admin-only staff creation, JWT bearer tokens, `/api/auth/me`, forgot/reset password.
- Customer profiles: paginated and searchable staff CRUD with archive, and authenticated self-service profile updates.
- Products, variants, and subscription plans with validation and lifecycle permissions.
- Tax rules, discounts, quotations, reusable quotation templates, quote confirmation and conversion.
- Subscription creation and lifecycle actions (confirm, activate, pause, resume, close, renew).
- Recurring invoice creation on a scheduler with database uniqueness on subscription and billing period.
- Invoice and payment APIs, manual partial payments, overpayment prevention, unpaid invoice cancellation, invoice reminders, and PDF invoice/receipt downloads.
- Quotation PDFs and send/confirm/convert workflow, plus reusable quotation templates.
- Customer portal endpoints for own subscriptions, plans, invoices, payment history, and documents. Ownership checks use the authenticated account email.
- Revenue summary, MRR/ARR, collected and outstanding totals, overdue counts, monthly revenue CSV, plan distribution, activity, and report views for subscriptions, customers, invoices, payment methods, product performance, and discount usage.
- Admin-only audit history for quotation, subscription, billing, invoice, and payment events.
- Flyway schema migrations, demo records, environment-based configuration, Dockerfiles, and Compose.

All API paths use `/api`. Admin and staff business APIs enforce roles in Spring Security; tax configuration and staff management require ADMIN. Customer portal reads and actions scope records to the signed-in customer's own profile.

## Key endpoints

- `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/password/forgot`, `POST /api/auth/password/reset`, `GET /api/auth/me`
- `/api/customers`, `/api/products`, `/api/plans`, `/api/taxes`, `/api/discounts`
- `/api/quotation-templates`, `/api/quotations`, `/api/subscriptions`
- `/api/invoices`, `/api/payments`, `/api/reports`, `/api/notifications`
- `/api/portal/profile`, `/api/portal/plans`, `/api/portal/subscriptions`, `/api/portal/invoices`, `/api/portal/payments`

JWT access tokens expire after 24 hours by default (`JWT_EXPIRATION_MS`). The frontend stores the token in local storage for session continuity; business data is stored in the relational database.

## Project layout

- `frontend/` — React, TypeScript, Vite dashboard, sign-in, management views, and customer portal.
- `backend/` — Spring Boot REST API, JWT/Spring Security, JPA, scheduler, PDF generation, and Flyway.
- `docker-compose.yml` — MySQL, API, and Nginx frontend services.
- `.env.example` — local MySQL/SMTP environment variables.

## Verification and environment limits

`mvn test` and `npm run build` pass in this workspace. Flyway has been verified against H2. API smoke flows against H2 covered role denial, quote-to-subscription conversion, discount/tax calculation, recurring invoice creation, partial payment and overpayment rejection, customer-owned invoice PDF download, password reset, and seeded record counts. The recurring billing integration test confirms a second invoice request for the same subscription period is rejected.

MySQL 8 and Docker are not available in this workspace, so MySQL startup and Compose deployment have not been verified here. SMTP delivery requires real credentials. Demo data is development-only; it includes the three documented demo users, 11 customer profiles, 8 products, 6 plans, 10 subscriptions, 15 invoices, 12 payments, 5 discount rules, and reusable quotation templates. The existing admin pages provide the currently implemented API flows; advanced audit history, payment gateway integration, and full PDF exports beyond invoices and payment receipts are not implemented.
