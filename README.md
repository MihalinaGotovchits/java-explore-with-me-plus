# Explore With Me Plus
_Приложение-афиша, где пользователи могут предлагать мероприятия, собирать компанию для участия, комментировать события и взаимодействовать друг с другом._

## Используемые технологии:
* Java 11
* REST API
* Spring Boot
* Maven
* Микросервисная архитектура, Docker
* PostgreSQL
* JPA, Hibernate
* Lombok
* Postman

## Архитектура приложения:
Приложение состоит из нескольких модулей:
* **GateWay** - проверяет права пользователей и маршрутизирует запросы
* **Основной сервис** - содержит бизнес-логику приложения
* **Сервис статистики** - собирает аналитику по просмотрам и взаимодействиям
* **Сервис рейтингов** (новый) - управляет оценками и рейтингами

## API основного сервиса:
1. **Публичное API** (доступно всем):
    - Просмотр событий, категорий, подборок
    - Просмотр комментариев
    - Просмотр рейтингов

2. **Приватное API** (для авторизованных пользователей):
    - Управление событиями
    - Запросы на участие
    - Комментирование
    - Подписки на пользователей
    - Оценка событий (лайки/дизлайки)

3. **Административное API**:
    - Модерация событий с обратной связью
    - Управление категориями, пользователями
    - Управление подборками
    - Модерация комментариев

## Спецификация Swagger для API:
[Основной сервис](https://github.com/ValentinaBuddha/java-explore-with-me/blob/main/ewm-main-service-spec.json)  
[Сервис статистики](https://github.com/ValentinaBuddha/java-explore-with-me/blob/main/ewm-stats-service-spec.json)

## Postman тесты для сервисов:
[Основной сервис](https://github.com/ValentinaBuddha/java-explore-with-me/blob/main/postman/main.json)  
[Сервис статистики](https://github.com/ValentinaBuddha/java-explore-with-me/blob/main/postman/stat.json)  
[Функциональность комментирования](https://github.com/ValentinaBuddha/java-explore-with-me/blob/main/postman/feature.json)

## Иструкция по развертыванию
* mvn clean package
* mvn install
* docker-compose build
* docker-compose up -d
* основной сервис: http://localhost:8080
* сервис статистики: http://localhost:9090
