# Лог прогресса

## Текущее проверенное состояние

- Корень: `D:\apps\car_shop`
- Стенд: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Фича F-001: **в работе**; этапы 1–5 (конфигуратор/заказ) в этой сессии
- Следующий шаг: этап 6 — кабинеты customer/staff + смена статусов
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение) — этап 5

- Дата: 2026-10-28
- Цель: конфигуратор + создание заказа с серверным total
- Сделано:
  - `PriceCalculator` + unit tests
  - `POST /api/orders/quote` (public), `POST /api/orders` (auth), `GET /api/orders/{id}` (owner)
  - `GET /api/colors`, `GET /api/extra-services`
  - Отказ `claimedTotal` mismatch → 400; без auth create → 401
  - UI вкладка Configurator с server total
- Верификация:
  - `./mvnw test` — 12 OK (PriceCalculator + OrderIntegrationTest)
  - `pnpm test` / `pnpm build` — OK
- Риски: кабинеты «Мои заказы»/staff ещё на этапе 6; JWT только in-memory на UI
- Коммит этапа 5: по запросу пользователя

### Ранее

- Этапы 1–4 на origin (`087c92a` … `0154746`)
