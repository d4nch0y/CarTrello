# Ръководство за настройка на Docker Compose

## Основни принципи


Docker Compose позволява дефиниране и управление на приложения с множество контейнери. В случая имаме два основни сервиса:

База данни MySQL

Spring Boot приложение

## Компоненти на системата
### 1. MySQL база данни
Използва официален образ mysql:8

Конфигурира се с:

Име на база: CarTrello

Потребител: root

Парола: Dani24117318

Порт 3306 е изложен на хост машината

Данните се съхраняват в Docker volume db_data

2. Spring Boot приложение
   Създава се от Dockerfile в корена на проекта

Зависи от базата данни (depends_on: db)

Използва следните environment variables за връзка с DB:

SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/CarTrello

SPRING_DATASOURCE_USERNAME: root

SPRING_DATASOURCE_PASSWORD: Dani24117318

Порт 8080 е изложен на хост машината

## Изграждане и стартиране на Docker контейнерите

### 1. Предварителни изисквания
- Инсталиран Docker Desktop
- Инсталиран Docker Compose (включен в Docker Desktop)
- Копие на проекта (вкл. `docker-compose.yml` и `Dockerfile`)

## Структура на файловете


```bash

      C:.
        ├───.idea
        │   ├───inspectionProfiles
        │   └───shelf
        │       └───Changes
        └───demo
        └───demo
        ├───.idea
        ├───.mvn
        │   └───wrapper
        ├───src
        │   ├───main
        │   │   ├───java
        │   │   │   └───org
        │   │   │       └───dkn
        │   │   │           └───cartrello
        │   │   │               ├───config
        │   │   │               ├───controller
        │   │   │               ├───model
        │   │   │               ├───repository
        │   │   │               └───service
        │   │   └───resources
        │   │       ├───static
        │   │       │   └───upload
        │   │       └───templates
        │   └───test
        │       └───java
        │           └───org
        │               └───dkn
        │                   └───cartrello
        └───target
        ├───classes
        │   ├───org
        │   │   └───dkn
        │   │       └───cartrello
        │   │           ├───config
        │   │           ├───controller
        │   │           ├───model
        │   │           ├───repository
        │   │           └───service
        │   ├───static
        │   │   └───upload
        │   └───templates
        ├───generated-sources
        │   └───annotations
        ├───generated-test-sources
        │   └───test-annotations
        └───test-classes
        └───CarTrello
        └───demo
```
## Пример за създаване на docker-compose

Стартиране на docker
docker build -t cartrllo


Създаване на docker-composed
docker-compose up --build 




## Примерен docker-compose.yml файл

```yaml
    version: '3.8'
    
    services:
  db:
    image: mysql:8
    container_name: cartrllo-db
    environment:
      MYSQL_DATABASE: CarTrello
      MYSQL_ROOT_USER: root
      MYSQL_ROOT_PASSWORD: Dani24117318
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - cartrllo-net

  app:
    build: .
    container_name: cartrllo-app
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/CarTrello
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: Dani24117318
    networks:
      - cartrllo-net

volumes:
  db_data:

networks:
  cartrllo-net:
```



