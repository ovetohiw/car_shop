# Декомпозиция: Car Shop MVP (F-001)

Контекст цели и скоупа — в [2026-10-27-car-shop-mvp-plan.md](2026-10-27-car-shop-mvp-plan.md). Не терять: UI :8080, API :8081, Oracle :1521, JWT customer/staff, серверный расчёт цены, эмуляция исполнения.

## Этапы

### 1. Каркас репозитория и Docker Compose

- Создать `frontend/` (React + Vite + TypeScript + Tailwind), `backend/` (Spring Boot)
- Корневой `docker-compose.yml`: frontend 8080, backend 8081, oracle 1521
- Dockerfile для frontend и backend; `.env.example` без секретов
- Критерий этапа: `docker compose up --build` поднимает три сервиса (backend может ждать готовности Oracle)

### 2. Схема Oracle, мигра, сиды, картинки

- Сущности: User, CarModel, Trim, Color, ExtraService, Part, Order, OrderItem
- Миграции Flyway или Liquibase; seed staff + демо-каталог
- Картинки в `frontend/public/images/cars/` (и запчасти при необходимости); URL в сидах
- Критерий этапа: backend стартует, схема применена, сиды читаются

### 3. Auth: Spring Security + JWT

- Регистрация, логин, выдача JWT; роли `customer` / `staff`
- Защита API; 401 без токена; 403 для не-staff на staff-маршрутах
- Критерий этапа: интеграционные тесты auth зелёные

### 4. REST каталога авто и запчастей

- Публичные эндпоинты списка/карточки авто и запчастей с image URL
- Критерий этапа: API отдаёт сиды; фронт может отрисовать каталог (хотя бы сырой)

### 5. Конфигуратор и создание заказа

- Расчёт итога только на сервере (base + trim + color + extras + parts)
- Создание заказа с snapshot конфигурации; отказ при подмене цены
- Критерий этапа: unit на расчёт цены; заказ сохраняется в Oracle

### 6. Кабинеты customer и staff

- Customer: «Мои заказы»
- Staff: все заказы; смена статусов `new` → `submitted` → `in_fulfillment` → `completed` / `cancelled`
- Критерий этапа: staff-flow работает через API

### 7. Frontend UI на :8080

- Страницы: каталог, запчасти, конфигуратор, auth, мои заказы, staff
- Интеграция с API :8081 (env/proxy)
- Критерий этапа: сценарий через UI на 8080

### 8. Health-check, тесты, DoD

- `GET /api/health`; JUnit; Vitest/Testing Library; Playwright e2e
- Отметить критерии в FEATURE.md; заполнить «Доказательства DoD»
- Критерий этапа: верификация из AGENTS.md зелёная; статус фичи → `готово`

## Статус этапов

| # | Этап                         | Статус    |
|---|------------------------------|-----------|
| 1 | Каркас + Compose             | готов     |
| 2 | Oracle / JPA / сиды / фото   | готов     |
| 3 | Auth JWT                     | готов     |
| 4 | Каталог REST                 | готов     |
| 5 | Конфигуратор + заказ         | не начат  |
| 6 | Кабинеты                     | не начат  |
| 7 | Frontend UI                  | не начат  |
| 8 | Тесты и DoD                  | не начат  |
