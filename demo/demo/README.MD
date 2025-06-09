# Ръководство за настройка на Docker Compose

## Основни принципи

Docker Compose ви позволява да дефинирате и управлявате приложения с множество контейнери. Ключови концепции:

markdown
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

## Пример за създаване на docker-compose

Start containers in detached mode
docker-compose up -d

Stop containers
docker-compose down

Rebuild containers
docker-compose up -d --build

View logs
docker-compose logs -f

Execute command in running container
docker-compose exec app bash


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



