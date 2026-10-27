# Лог прогресса

## Текущее проверенное состояние

- Корень репозитория: `D:\apps\car_shop`
- Старт: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Верификация: `docker compose ps` + `curl http://localhost:8081/api/health` + тесты из AGENTS.md
- Фича: F-001 (статус **в работе**); этапы 1–3 готовы
- Следующий шаг: этап 4 — REST каталога авто/запчастей
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение)

- Дата: 2026-10-28
- Цель: Этап 3 — Spring Security + JWT
- Сделано:
  - `POST /api/auth/register` (только CUSTOMER), `POST /api/auth/login`, JWT Bearer filter
  - `GET /api/staff/ping` (ROLE_STAFF); 401/403 handlers
  - AuthIntegrationTest (Oracle localhost:1521), JwtServiceTest
  - Instant→LocalDateTime для Oracle TIMESTAMP
- Запущенная верификация:
  - `./mvnw test` — OK (5 tests)
  - Compose backend rebuild — OK
  - curl: health UP; login staff → token; staff ping 200; anon 401; customer staff ping 403; register 201
- Риски:
  - Auth IT требуют поднятый Oracle из compose
  - JWT revocation = expiry only (без denylist)
  - Хранение токена на UI ещё не решено (этап 7)
  - Testcontainers на этой машине не видит Docker npipe — IT идут через host :1521
- Коммиты этапа 3: не создавались в этом шаге (нужен явный запрос)
- Следующий лучший шаг: этап 4 — REST каталога

### Сессия 001–002 ранее

- Этапы 1–2: scaffold + Flyway/JPA/сиды; коммиты `087c92a`, `8283a92` на origin/main
