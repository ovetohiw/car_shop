# Лог прогресса

## Текущее проверенное состояние

- Корень репозитория: `D:\apps\car_shop`
- Стандартный путь старта: `docker compose up --build` → UI http://localhost:8080, API http://localhost:8081, Oracle :1521
- Стандартный путь верификации: `docker compose ps` + `curl http://localhost:8081/api/health` + тесты из AGENTS.md
- Текущая незавершённая фича с наивысшим приоритетом: F-001 MVP онлайн-магазина (статус: **в работе**)
- Текущий блокер: нет; этап 1 каркаса завершён. Следующий шаг — этап 2 (схема Oracle / JPA / сиды / фото). Flyway vs Liquibase ещё не выбран.

## Лог сессий

### Сессия 002

- Дата: 2026-10-28
- Цель: Этап 1 — каркас frontend/backend + Docker Compose
- Сделано:
  - Выбраны Maven и образ `gvenzl/oracle-xe:21-slim-faststart`
  - Созданы `backend/` (Spring Boot 3.4, Java 17, `GET /api/health`), `frontend/` (React+Vite+TS+Tailwind+Vitest+ESLint/Prettier)
  - Корневые `docker-compose.yml`, `.env.example`, `.gitignore`, Dockerfiles
  - F-001 → `в работе`
- Запущенная верификация:
  - `cd backend && ./mvnw test` — OK (HealthControllerTest)
  - `cd frontend && pnpm test` — OK; `pnpm build` — OK
  - `docker compose up --build -d` — OK: oracle healthy, backend/frontend up
  - `curl http://localhost:8081/api/health` → `{"status":"UP"}` HTTP 200
  - `curl http://localhost:8080/` → HTTP 200
- Зафиксированные доказательства: этап 1 декомпозиции выполнен; DoD всей F-001 ещё нет
- Коммиты: не создавались (нет явного запроса)
- Обновлённые файлы или артефакты:
  - `backend/**`, `frontend/**`, `docker-compose.yml`, `.env.example`, `.gitignore`, `README.md`
  - `FEATURE.md`, `STACK.md`, `AGENTS.md`, `PROGRESS.md`
  - exec plan / decompoze
- Известный риск или нерешённая проблема:
  - Oracle: высокий RAM / долгий cold start (на этой машине health прошёл ~1.5 мин после старта)
  - В Docker-сборке frontend временно `minimum-release-age 0` из‑за политики pnpm на свежие транзитивные пакеты
  - Flyway vs Liquibase — открыто до этапа 2
  - `.env` локальный (в `.gitignore`); не коммитить
- Следующий лучший шаг: этап 2 — схема БД, JPA, миграции, сиды, картинки

### Сессия 001

- Дата: 2026-10-27
- Цель: Заполнить FEATURE.md, STACK.md, AGENTS.md и exec plan под Car Shop MVP
- Сделано:
  - Зафиксирован стек в STACK.md; ТЗ F-001; exec plan + decompoze
- Запущенная верификация: не применимо
- Коммиты: не создавались
- Следующий лучший шаг (на тот момент): этап 1 scaffold — выполнен в сессии 002
