# Лог прогресса

## Текущее проверенное состояние

- Корень репозитория: `D:\apps\car_shop`
- Стандартный путь старта: `docker compose up --build` → UI http://localhost:8080, API http://localhost:8081, Oracle :1521
- Стандартный путь верификации: `docker compose ps` + `curl http://localhost:8081/api/health` + тесты из AGENTS.md
- Текущая незавершённая фича с наивысшим приоритетом: F-001 MVP онлайн-магазина (статус: **в работе**)
- Текущий блокер: нет. Этапы 1–2 готовы. Следующий шаг — этап 3 (Auth JWT).

## Лог сессий

### Сессия 002

- Дата: 2026-10-28
- Цель: Этап 1 (каркас) + этап 2 (Oracle/JPA/сиды/фото); commit/push
- Сделано:
  - Этап 1: `frontend/`, `backend/`, Compose, health; Maven + `gvenzl/oracle-xe:21-slim-faststart`
  - Этап 2: Flyway V1 schema + V2 seed; JPA entities/repos; SVG images; SeedIntegrityChecker
  - F-001 → `в работе`
- Запущенная верификация:
  - `./mvnw test` — OK
  - `pnpm test` / `pnpm build` — OK (этап 1)
  - `docker compose up --build` — OK; Flyway applied v1+v2
  - `/api/health` → UP
  - Backend log: `Demo seeds readable: staff=1 cars=3 trims=6 colors=4 extras=3 parts=3`
- Зафиксированные доказательства: этапы 1–2 декомпозиции; DoD всей F-001 ещё нет
- Коммиты: scaffold этап 1 запушен (`087c92a`); этап 2 — в этом же продолжении сессии
- Известный риск или нерешённая проблема:
  - Oracle: RAM / cold start
  - Docker frontend: `minimum-release-age 0` для pnpm
  - Демо-пароль staff только для локалки; auth ещё не проверяет его (этап 3)
  - `.env` локальный, не в git
- Следующий лучший шаг: этап 3 — Spring Security + JWT

### Сессия 001

- Дата: 2026-10-27
- Цель: FEATURE/STACK/AGENTS/exec plan под Car Shop MVP
- Сделано: документация и план F-001
- Следующий лучший шаг (тогда): этап 1 — выполнен
