# Лог прогресса

## Текущее проверенное состояние

- Корень: `D:\apps\car_shop`
- Стенд: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Фича F-001: **в работе**; этапы 1–6 готовы
- Следующий шаг: этап 7 — полировка Frontend UI на :8080
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение) — этап 6

- Дата: 2026-10-28
- Цель: кабинеты customer/staff + смена статусов эмуляции
- Сделано:
  - `GET /api/orders/mine`, `GET /api/staff/orders`, `PATCH /api/staff/orders/{id}/status`
  - `OrderStatusTransitions` NEW→SUBMITTED→IN_FULFILLMENT→COMPLETED|/CANCELLED
  - UI вкладки My orders / Staff; auth вынесен в App
  - `StaffCabinetIntegrationTest` + `OrderStatusTransitionsTest`
- Верификация:
  - `./mvnw test` — OK (включая StaffCabinet IT)
  - `pnpm test` / `pnpm build` — OK
  - `docker compose up --build`; `/api/health` → UP
- Риски: UI ещё сырой (этап 7); JWT только in-memory
- Коммит этапа 6: по запросу пользователя

### Сессия 002 — этап 5

- Дата: 2026-10-28
- Цель: конфигуратор + создание заказа с серверным total
- Сделано: PriceCalculator, quote/create, UI configurator
- Коммит: `957c014`

### Ранее

- Этапы 1–4 на origin (`087c92a` … `0154746`)
