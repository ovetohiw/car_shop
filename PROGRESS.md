# Лог прогресса

## Текущее проверенное состояние

- Корень: `D:\apps\car_shop`
- Стенд: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Фича F-001: **в работе**; этапы 1–7 готовы
- Следующий шаг: этап 8 — health/тесты/DoD, закрытие F-001
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение) — этап 7

- Дата: 2026-10-28
- Цель: Frontend UI на :8080 (страницы + сценарий через UI)
- Сделано:
  - React Router: `/`, `/parts`, `/configure`, `/orders`, `/staff`
  - Shell с auth; hero каталога; отдельная страница Parts
  - Syne/Manrope, токены CSS, состояния loading/empty/error
  - Vitest + Playwright smoke сценарий на :8080
- Верификация:
  - `pnpm test` / `pnpm build` — OK
  - `pnpm test:e2e` против localhost:8080 — OK
  - compose + `/api/health` → UP
- Риски: JWT in-memory; полный DoD/e2e заказа — этап 8
- Коммит: по запросу пользователя (push в конце этапа)

### Ранее

- Этапы 1–6 на origin (`087c92a` … `dc637cd`)
