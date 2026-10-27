## Описание проекта

- Онлайн-магазин продажи автомобилей и запчастей: каталог с фото, конфигуратор заказа, auth (customer/staff), кабинет заказов и эмуляция исполнения.
- Стэк: STACK.md.
- Порты стенда: frontend 8080, backend 8081, Oracle 1521.

## Стартовый воркфлоу

- Прочитать файл PROGRESS.md;
- Прочитать файл PLANS.md;
- Прочитать файл FEATURE.md;
- Посмотреть последние коммиты: git log --oneline -5;

Если текущая базовая верификация падает то нужно чинить ее. Не выполнять работу при неисправном стартовом состоянии.

## Тест работоспособности приложения

- Поднять приложение: `docker compose up --build` (UI http://localhost:8080, API http://localhost:8081, Oracle :1521).
- Прогнать тесты: backend — `cd backend && ./mvnw test` (auth IT требуют Oracle на localhost:1521 из compose); frontend — `cd frontend && pnpm test` и `cd frontend && pnpm test:e2e`.
- Верификация: `docker compose ps` (сервисы healthy) && `curl http://localhost:8081/api/health` && прогон тестов выше.

## Definition of Done

- Фича действительно реализована;
- Проверка фичи сквозными тестами;
- Отсутствие ошибок;
- health-check;
- Доказательство зафиксированно в FEATURE.md.

## Рабочие правила

- Работать над одной фичей за раз;
- Не помечать фичу сделанной если написан только код;
- Папка с референсами для дизайна - refs. Допускается, что папка может быть пустая.
- Соблюдать docs/convesion/SECURITY.md.
- Соблюдать docs/convesion/TEST.md.
- Соблюдать docs/convesion/DEVELOPMENT.md.
- Соблюдать docs/convesion/LOGGING.md.
- Соблюдать docs/convesion/NETWORK.md.
- Соблюдать docs/convesion/DB.md.
- Соблюдать docs/convesion/ARCHITECTURE.md.
- Соблюдать docs/convesion/UIDESIGN.md.

## Окончание сессии

- Обнови PROGRESS.md;
- Обнови FEATURE.md;
- Обнови файлы в docs/exec-plans;
- Обнови файлы в ranking;
- Опиши все риски и нерешенные вопросы;
- Закоммить все измененения;
- Сохрани чистоту репозитория чтобы следующая сессия могла работать;
- Если изменился стэк обнови STACK.md;
- Обнови README.md.
