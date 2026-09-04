-- ============================================================
-- Schema: dashboard evento congresso
-- Modello: partecipanti (anagrafica) + touchpoint_definizioni
-- (dizionario dal foglio 01) + partecipante_touchpoint (fatti)
-- ============================================================

DROP TABLE IF EXISTS partecipante_touchpoint;
DROP TABLE IF EXISTS touchpoint_definizioni;
DROP TABLE IF EXISTS partecipanti;

-- --------------------------------------------------------------
-- ANAGRAFICA
-- Una riga per persona. Include anche i campi "denormalizzati"
-- che servono per il funnel di sessione (permanenza, focus_rate,
-- quiz) perché sono attributi 1:1 con la persona, non eventi
-- ripetibili: tenerli qui evita join inutili sulle query più
-- frequenti della dashboard (funnel, andamento per giornata).
-- --------------------------------------------------------------
CREATE TABLE partecipanti (
    id                      INTEGER PRIMARY KEY,
    nome_cognome            TEXT NOT NULL,
    email                   TEXT NOT NULL UNIQUE,
    tipologia_stakeholder   TEXT NOT NULL,
    regione                 TEXT NOT NULL,
    canale_ingaggio         TEXT NOT NULL,

    in_database_dem         BOOLEAN NOT NULL,
    dem_inviata             BOOLEAN NOT NULL,
    dem_consegnata          BOOLEAN NOT NULL,
    dem_aperta              BOOLEAN NOT NULL,

    li_annuncio_reach        BOOLEAN NOT NULL,
    li_annuncio_interazione  BOOLEAN NOT NULL,
    li_recap_reach           BOOLEAN NOT NULL,
    li_recap_interazione     BOOLEAN NOT NULL,

    visita_stand             BOOLEAN NOT NULL,
    giorno_visita             DATE,
    visualizzazioni           INTEGER NOT NULL DEFAULT 0,
    scroll_approfondimento    INTEGER NOT NULL DEFAULT 0,

    accesso_sala_vip          BOOLEAN NOT NULL,
    risposte_wordcloud        INTEGER NOT NULL DEFAULT 0,

    presenza_simposio         BOOLEAN NOT NULL,
    permanenza_min             NUMERIC(5,2),
    focus_rate                 NUMERIC(5,4),
    quiz_completati             INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_partecipanti_tipologia ON partecipanti (tipologia_stakeholder);
CREATE INDEX idx_partecipanti_regione   ON partecipanti (regione);
CREATE INDEX idx_partecipanti_canale    ON partecipanti (canale_ingaggio);
CREATE INDEX idx_partecipanti_giorno    ON partecipanti (giorno_visita);

-- --------------------------------------------------------------
-- DIZIONARIO TOUCHPOINT (dal foglio 01_Interazioni)
-- Classifica ogni touchpoint per fase del percorso: questa è la
-- tabella che rende possibile "il funnel per fase" senza
-- hardcodare l'elenco delle colonne nel codice applicativo.
-- --------------------------------------------------------------
CREATE TABLE touchpoint_definizioni (
    nome_tecnico    TEXT PRIMARY KEY,
    intestazione    TEXT NOT NULL,
    tipo_dato       TEXT NOT NULL,
    fase_percorso   TEXT NOT NULL,
    descrizione     TEXT
);

-- --------------------------------------------------------------
-- FATTI TOUCHPOINT (vista "long"/EAV)
-- Una riga per ogni touchpoint booleano/conteggio verificatosi
-- per una persona. Generata dall'ETL a partire dalla tabella
-- wide 'partecipanti' incrociando con 'touchpoint_definizioni'.
-- --------------------------------------------------------------
CREATE TABLE partecipante_touchpoint (
    id               BIGSERIAL PRIMARY KEY,
    partecipante_id  INTEGER NOT NULL REFERENCES partecipanti(id),
    nome_tecnico     TEXT NOT NULL REFERENCES touchpoint_definizioni(nome_tecnico),
    avvenuto         BOOLEAN NOT NULL,
    valore_numerico  NUMERIC,
    valore_data      DATE
);

CREATE INDEX idx_fatti_partecipante ON partecipante_touchpoint (partecipante_id);
CREATE INDEX idx_fatti_touchpoint   ON partecipante_touchpoint (nome_tecnico);
