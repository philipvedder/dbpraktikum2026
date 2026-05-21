DROP SCHEMA IF EXISTS media_store CASCADE;
CREATE SCHEMA media_store;
SET search_path TO media_store;


-- ENUMS

CREATE TYPE produkttyp_enum AS ENUM (
    'BUCH',
    'MUSIK_CD',
    'DVD'
);

CREATE TYPE dvd_rolle_enum AS ENUM (
    'ACTOR',
    'CREATOR',
    'DIRECTOR'
);


-- MAIN

CREATE TABLE produkt (
    produkt_nr        BIGINT,
    produkttyp        produkttyp_enum NOT NULL,
    titel             TEXT NOT NULL,
    verkaufsrang      INTEGER,
    bild_url          TEXT,

    CONSTRAINT pk_produkt PRIMARY KEY (produkt_nr),
);


CREATE TABLE person (
    person_id         BIGINT GENERATED ALWAYS AS IDENTITY,
    name              TEXT NOT NULL,

    CONSTRAINT pk_person PRIMARY KEY (person_id)
);


CREATE TABLE verlag (
    verlag_id         BIGINT GENERATED ALWAYS AS IDENTITY,
    name              TEXT NOT NULL UNIQUE,

    CONSTRAINT pk_verlag PRIMARY KEY (verlag_id)
);


-- SUB

CREATE TABLE buch (
    produkt_nr          BIGINT,
    seitenzahl          INTEGER NOT NULL,
    erscheinungsdatum   DATE NOT NULL,
    isbn                VARCHAR(32) NOT NULL UNIQUE,
    verlag_id           BIGINT NOT NULL,

    CONSTRAINT pk_buch PRIMARY KEY (produkt_nr),

    CONSTRAINT fk_buch_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_buch_verlag FOREIGN KEY (verlag_id) REFERENCES verlag (verlag_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT uq_buch_isbn UNIQUE (isbn),
    CONSTRAINT chk_buch_seitenzahl_pos CHECK (seitenzahl > 0),
    CONSTRAINT chk_buch_erscheinungsdatum_nicht_zukunft CHECK (erscheinungsdatum <= CURRENT_DATE)
);


CREATE TABLE musik_cd (
    produkt_nr          BIGINT,
    label               TEXT NOT NULL,
    erscheinungsdatum   DATE NOY NULL,

    CONSTRAINT pk_musik_cd PRIMARY KEY (produkt_nr),

    CONSTRAINT fk_musik_cd_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT chk_musik_cd_erscheinungsdatum_nicht_zukunft CHECK (erscheinungsdatum <= CURRENT_DATE)
);


CREATE TABLE dvd (
    produkt_nr          BIGINT,
    format              VARCHAR(32) NOT NULL,
    laufzeit_minuten    INTEGER NOT NULL,
    region_code         VARCHAR(32) NOT NULL,

    CONSTRAINT pk_dvd PRIMARY KEY (produkt_nr),

    CONSTRAINT fk_dvd_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT chk_dvd_laufzeit_pos CHECK (laufzeit_minuten > 0)
);


-- Personen

CREATE TABLE buch_autor (
    produkt_nr      BIGINT,
    person_id       BIGINT,

    CONSTRAINT pk_buch_autor PRIMARY KEY (produkt_nr, person_id),

    CONSTRAINT fk_buch_autor_buch FOREIGN KEY (produkt_nr) REFERENCES buch (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_buch_autor_person FOREIGN KEY (person_id) REFERENCES person (person_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE musik_cd_kuenstler (
    produkt_nr      BIGINT,
    person_id       BIGINT,

    CONSTRAINT pk_musik_cd_kuenstler PRIMARY KEY (produkt_nr, person_id),

    CONSTRAINT fk_musik_cd_kuenstler_musik_cd FOREIGN KEY (produkt_nr) REFERENCES musik_cd (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_musik_cd_kuenstler_person FOREIGN KEY (person_id) REFERENCES person (person_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE dvd_beteiligung (
    produkt_nr      BIGINT,
    person_id       BIGINT,
    rolle           dvd_rolle_enum NOT NULL,

    CONSTRAINT pk_dvd_beteiligung PRIMARY KEY (produkt_nr, person_id, rolle),

    CONSTRAINT fk_dvd_beteiligung_dvd FOREIGN KEY (produkt_nr) REFERENCES dvd (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_dvd_beteiligung_person FOREIGN KEY (person_id) REFERENCES person (person_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Titel

CREATE TABLE musik_cd_titel (
    track_id        BIGINT GENERATED ALWAYS AS IDENTITY,
    produkt_nr      BIGINT NOT NULL,
    name            TEXT NOT NULL,

    CONSTRAINT pk_musik_cd_titel PRIMARY KEY (track_id),

    CONSTRAINT fk_musik_cd_titel_musik_cd FOREIGN KEY (produkt_nr) REFERENCES musik_cd (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,
);


-- Kategorie

CREATE TABLE kategorie (
    kategorie_id            BIGINT GENERATED ALWAYS AS IDENTITY,
    name                    TEXT NOT NULL,
    parent_kategorie_id     BIGINT,

    CONSTRAINT pk_kategorie PRIMARY KEY (kategorie_id),

    CONSTRAINT fk_kategorie_parent FOREIGN KEY (parent_kategorie_id) REFERENCES kategorie (kategorie_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT chk_kategorie_nicht_eigener_parent CHECK (parent_kategorie_id IS NULL OR parent_kategorie_id <> kategorie_id),

    -- Keine gleichnamigen Geschwister
    CONSTRAINT uq_kategorie_parent_name UNIQUE (parent_kategorie_id, name)
);


CREATE TABLE produkt_kategorie (
    produkt_nr      BIGINT,
    kategorie_id    BIGINT,

    CONSTRAINT pk_produkt_kategorie PRIMARY KEY (produkt_nr, kategorie_id),

    CONSTRAINT fk_produkt_kategorie_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_produkt_kategorie_kategorie FOREIGN KEY (kategorie_id) REFERENCES kategorie (kategorie_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);


-- Similar Products

CREATE TABLE aehnliches_produkt (
    produkt_nr_1    BIGINT,
    produkt_nr_2    BIGINT,

    CONSTRAINT pk_aehnliches_produkt PRIMARY KEY (produkt_nr_1, produkt_nr_2),

    CONSTRAINT fk_aehnliches_produkt_1 FOREIGN KEY (produkt_nr_1) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_aehnliches_produkt_2 FOREIGN KEY (produkt_nr_2) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    -- Kein Verweis auf self, und verhindert Redundanz
    CONSTRAINT chk_aehnliches_produkt_geordnet CHECK (produkt_nr_1 < produkt_nr_2)
);


-- Filialen

CREATE TABLE filiale (
    filiale_id      BIGINT GENERATED ALWAYS AS IDENTITY,
    name            VARCHAR(256) NOT NULL,
    strasse         VARCHAR(256) NOT NULL,
    hausnummer      VARCHAR(6) NOT NULL,
    plz             VARCHAR(16) NOT NULL,
    ort             VARCHAR(256) NOT NULL,
    land            VARCHAR(256) NOT NULL,

    CONSTRAINT pk_filiale PRIMARY KEY (filiale_id),
);


CREATE TABLE angebot (
    filiale_id      BIGINT,
    produkt_nr      BIGINT,
    preis           NUMERIC,
    zustand         TEXT NOT NULL,

    CONSTRAINT pk_angebot PRIMARY KEY (filiale_id, produkt_nr),

    CONSTRAINT fk_angebot_filiale FOREIGN KEY (filiale_id) REFERENCES filiale (filiale_id)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_angebot_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT chk_angebot_preis_pos CHECK (preis IS NULL OR preis > 0)
);


-- Kunden

CREATE TABLE kunde (
    kunde_id            BIGINT GENERATED ALWAYS AS IDENTITY,
    name                VARCHAR(256) NOT NULL,

    CONSTRAINT pk_kunde PRIMARY KEY (kunde_id)
);


CREATE TABLE bestellung (
    bestellung_id      BIGINT GENERATED ALWAYS AS IDENTITY,
    kunde_id           BIGINT NOT NULL,
    produkt_id         BIGINT NOT NULL,
    kaufzeitpunkt      TIMESTAMP NOT NULL,
    kontonummer        VARCHAR(256) NOT NULL,
    strasse            VARCHAR(256) NOT NULL,
    hausnummer         VARCHAR(6) NOT NULL,
    plz                VARCHAR(16) NOT NULL,
    ort                VARCHAR(256) NOT NULL,
    land               VARCHAR(256) NOT NULL,
    kontonummer        VARCHAR(256) NOT NULL,

    CONSTRAINT pk_bestellung PRIMARY KEY (bestellung_id),

    CONSTRAINT fk_bestellung_kunde FOREIGN KEY (kunde_id) REFERENCES kunde (kunde_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_bestellung_produkt FOREIGN KEY (produkt_id) REFERENCES produkt (produkt_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT chk_bestellung_kaufzeitpunkt_nicht_zukunft CHECK (kaufzeitpunkt <= CURRENT_TIMESTAMP)
);

-- Rezensionen

CREATE TABLE rezension (
    rezension_id            BIGINT GENERATED ALWAYS AS IDENTITY,
    kunde_id                BIGINT NOT NULL,
    produkt_nr              BIGINT NOT NULL,
    rezensionszeitpunkt     TIMESTAMP NOT NULL,
    punkte                  INTEGER NOT NULL,
    rezensionstext          TEXT,

    CONSTRAINT pk_rezension PRIMARY KEY (rezension_id),

    CONSTRAINT fk_rezension_kunde FOREIGN KEY (kunde_id) REFERENCES kunde (kunde_id)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_rezension_produkt FOREIGN KEY (produkt_nr) REFERENCES produkt (produkt_nr)
    ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT chk_rezension_punkte CHECK (punkte BETWEEN 1 AND 5),
    CONSTRAINT chk_rezension_zeitpunkt_nicht_zukunft CHECK (rezensionszeitpunkt <= CURRENT_TIMESTAMP),
);
