# Car Shop

Онлайн-магазин автомобилей и запчастей (MVP): каталог, конфигуратор заказа, JWT auth (customer/staff), кабинеты заказов и эмуляция исполнения.

## Стек

- Frontend: React + Vite + TypeScript + Tailwind (порт **8080**)
- Backend: Java 17 + Spring Boot + Maven (порт **8081**)
- БД: Oracle XE (`gvenzl/oracle-xe:21-slim-faststart`, порт **1521**) + Flyway
- Запуск: Docker Compose

Демо staff (после сидов): `staff@carshop.local` / `StaffDemo1!` — только для локального стенда.

Auth API: `POST /api/auth/register`, `POST /api/auth/login` → JWT (`Authorization: Bearer …`). Staff probe: `GET /api/staff/ping`.

Подробности: [STACK.md](STACK.md), ТЗ: [FEATURE.md](FEATURE.md).

## Быстрый старт

1. Скопируйте `.env.example` → `.env` и замените `CHANGE_ME`.
2. Поднимите стенд:

```bash
docker compose up --build
```

- UI: http://localhost:8080  
- API health: http://localhost:8081/api/health  
- Oracle: localhost:1521  

Первый старт Oracle может занять несколько минут (healthcheck в compose).

## Тесты

```bash
cd backend && ./mvnw test
cd frontend && pnpm test
cd frontend && pnpm test:e2e
```

## Структура

- `frontend/` — React SPA
- `backend/` — Spring Boot REST
- `docs/exec-plans/inprogress/2026-10-27-car-shop-mvp/` — план F-001
