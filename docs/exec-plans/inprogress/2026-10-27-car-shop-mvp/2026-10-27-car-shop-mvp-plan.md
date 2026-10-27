# Exec plan: Car Shop MVP (F-001)

## Цель

Реализовать MVP онлайн-магазина автомобилей и запчастей: React SPA + Spring Boot + Oracle, полный стенд через Docker Compose (UI :8080, API :8081, Oracle :1521), auth с ролями customer/staff, конфигуратор с серверным расчётом цены, заказы и эмуляция исполнения в staff-кабинете.

Контракт фичи: [FEATURE.md](../../../../FEATURE.md) (F-001). Стек: [STACK.md](../../../../STACK.md).

## Скоуп

В скоупе:

- Каркас `frontend/`, `backend/`, корневой `docker-compose.yml`
- Oracle + JPA/Hibernate, миграции (Flyway или Liquibase), сиды, картинки авто
- Spring Security + JWT, роли `customer` / `staff`
- REST: каталог авто и запчастей, конфигуратор, заказы, staff-статусы
- UI: каталог, запчасти, конфигуратор, кабинеты customer и staff
- Health-check, unit/integration/e2e, доказательства DoD в FEATURE.md

Вне скоупа: платежи, доставка, OAuth соцсетей, админ-CRUD каталога, микросервисы, мультивалютность, мобильные приложения.

## Порты и инфраструктура

| Сервис    | Порт хоста | Назначение          |
|-----------|------------|---------------------|
| frontend  | 8080       | React SPA           |
| backend   | 8081       | Spring Boot REST    |
| oracle    | 1521       | Oracle DB           |

- Образ Oracle (кандидат): `gvenzl/oracle-xe` — подтвердить при scaffold; креды только в `.env` / `.env.example`.
- Compose: healthcheck Oracle, `depends_on` с condition; volume для данных.
- Миграции/seed на старте backend.

## Путь верификации

Команды — из [AGENTS.md](../../../../AGENTS.md):

1. `docker compose up --build` → UI http://localhost:8080, API http://localhost:8081
2. `curl http://localhost:8081/api/health`
3. Backend: `cd backend && ./mvnw test` (или Gradle после выбора)
4. Frontend: `cd frontend && pnpm test` и `pnpm test:e2e`
5. Сквозной сценарий: регистрация → конфигуратор → заказ → staff меняет статус

## Риски и блокеры

- Oracle: тяжёлый образ, долгий cold start, лицензии/RAM
- Конфликт портов на машине разработчика (8080/8081/1521)
- Большой MVP как одна фича — не дробить без смены статуса в FEATURE.md
- Секреты JWT/БД не коммитить

## Открытые решения

- Maven vs Gradle — **решено: Maven** (`backend/mvnw`)
- Образ Oracle — **решено: `gvenzl/oracle-xe:21-slim-faststart`**
- Flyway vs Liquibase — **решено: Flyway**

## Текущий шаг

Этап 4: публичный REST каталога авто и запчастей (список/карточка + image URL).

## Лог прогресса

- 2026-10-27: Создан exec plan; ТЗ F-001 в статусе `черновик`; стек зафиксирован (Spring Boot, React/Vite, Oracle, Docker Compose).
- 2026-10-28: Этап 1 **готов** — scaffold `frontend/` + `backend/` + Compose; `docker compose up --build` поднимает 3 сервиса; `/api/health` → UP; F-001 → `в работе`; Maven + `gvenzl/oracle-xe:21-slim-faststart`.
- 2026-10-28: Этап 2 **готов** — Flyway V1/V2, JPA, сиды; лог `Demo seeds readable: staff=1 cars=3 trims=6 colors=4 extras=3 parts=3`; health UP.
- 2026-10-28: Этап 3 **готов** — JWT register/login; staff ping 200 / anon 401 / customer 403; `./mvnw test` зелёный (AuthIntegrationTest + JwtServiceTest).
