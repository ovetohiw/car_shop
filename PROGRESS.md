# Лог прогресса

## Текущее проверенное состояние

- Корень: `D:\apps\car_shop`
- Стенд: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Фича F-001: **в работе**; этапы 1–4 готовы
- Следующий шаг: этап 5 — конфигуратор + заказ (серверный total)
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение)

- Дата: 2026-10-28
- Цель: commit/push этапа 3 + этап 4 REST каталога
- Сделано:
  - Этап 3 закоммичен/запушен (`07292ef`)
  - `GET /api/cars`, `/api/cars/{id}` (с trims), `/api/parts`, `/api/parts/{id}` — публичные
  - UI каталог cars/parts; nginx proxy `/api` → backend
- Верификация:
  - `./mvnw test` — 7 OK
  - `pnpm test` — OK
  - curl `/api/cars` и `/api/parts` → сиды; `http://localhost:8080/api/cars` через proxy → 200
- Риски: Auth/Catalog IT требуют Oracle :1521; полный UI-кабинет ещё на этапе 7
- Следующий шаг: этап 5

### Ранее

- Этапы 1–2: `087c92a`, `8283a92`
