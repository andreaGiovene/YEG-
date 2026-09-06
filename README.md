# Dashboard Evento Congresso - Factory Studios

Dashboard per l'analisi dei dati dell'evento congresso, con backend Spring Boot, database PostgreSQL, ETL Excel e frontend Angular.

## Avvio con Docker

Richiede Docker e Docker Compose.

```bash
docker compose up --build
```

Servizi avviati:

1. `db` - PostgreSQL con schema inizializzato da `db/schema.sql`
2. `etl` - caricamento una tantum del dataset Excel
3. `backend` - API Spring Boot su `http://localhost:8080/api`
4. `frontend` - dashboard Angular su `http://localhost:4200`

Per ricaricare il dataset dopo una modifica:

```bash
docker compose run --rm etl
```

Il caricamento e ripetibile: svuota le tabelle prima di inserire nuovamente i dati.

## Sviluppo locale

### Backend

Richiede Java 21 e PostgreSQL.

```bash
cd backend
mvn spring-boot:run
```

Le variabili di connessione sono configurabili tramite `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD`. Per eseguire il caricamento ETL:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=etl -Dspring-boot.run.arguments="--dataset=/percorso/Dataset_Evento_Congresso_2025.xlsx"
```

### Frontend

Richiede Node.js e npm.

```bash
cd frontend
npm ci
npm start
```

Il proxy di sviluppo inoltra le chiamate `/api` al backend su `localhost:8080`.

## Struttura dati

Il modello e composto da tre tabelle:

- `partecipanti` - anagrafica e touchpoint 1:1 del partecipante
- `touchpoint_definizioni` - dizionario dei touchpoint e fase del percorso
- `partecipante_touchpoint` - eventi touchpoint in formato long

Le query aggregate vengono eseguite dal backend tramite `JdbcTemplate`; il frontend riceve dati gia pronti per i grafici.

## Stack tecnologico

- Java 21 e Spring Boot 3
- Spring Data JPA e Spring Validation
- PostgreSQL
- Apache POI per l'ETL Excel
- Angular 18
- Chart.js tramite `ng2-charts`
- Docker Compose

## Verifiche

Backend:

```bash
cd backend
mvn clean test
```

Frontend:

```bash
cd frontend
npm ci
npm run build -- --configuration production
```

## Sviluppi futuri

- Aggiungere test automatici per repository, controller ed ETL
- Introdurre migrazioni versionate con Flyway
- Collegare la vista paginata dei partecipanti alla dashboard
- Aggiungere una cache per le aggregazioni
