# Лог прогресса

## Текущее проверенное состояние

- Корень: `D:\apps\car_shop`
- Стенд: `docker compose up --build` → UI :8080, API :8081, Oracle :1521
- Фича F-001: **готово** (этапы 1–8); план в `docs/exec-plans/completed/2026-10-27-car-shop-mvp/`
- Следующий шаг: новая фича (после переноса F-001 в лог FEATURE.md)
- Блокер: нет

## Лог сессий

### Сессия 002 (продолжение) — этап 8

- Дата: 2026-10-28
- Цель: health/тесты/DoD; закрытие F-001
- Сделано:
  - Playwright MVP e2e (guest catalog, register/order/cabinets/staff flow)
  - Полный прогон AGENTS verification
  - FEATURE.md критерии и DoD; статус `готово`
  - Exec plan перенесён в `docs/exec-plans/completed/`
- Верификация:
  - `./mvnw test` — 14 OK
  - `pnpm test` — 2 OK; `pnpm test:e2e` — 3 OK
  - `/api/health` → UP
- Риски (остаточные, не блокеры DoD): JWT in-memory на UI; Oracle cold start; нет Testcontainers
- Коммит: по запросу пользователя

### Ранее

- Этапы 1–7 на origin (`087c92a` … `d2e4970`)
