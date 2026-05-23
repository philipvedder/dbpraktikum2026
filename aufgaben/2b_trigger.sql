SET search_path TO media_store;

DROP TRIGGER IF EXISTS trg_rezension_bewertung ON rezension;
DROP FUNCTION IF EXISTS update_produkt_bewertung();


CREATE OR REPLACE FUNCTION update_produkt_bewertung()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE produkt
        SET durchschnittsbewertung = COALESCE((
            SELECT ROUND(AVG(punkte)::NUMERIC, 2)
            FROM rezension
            WHERE produkt_nr = NEW.produkt_nr
        ), 0)
        WHERE produkt_nr = NEW.produkt_nr;

        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        -- Falls die Rezension einem anderen Produkt zugeordnet wurde,
        -- muss auch das alte Produkt neu berechnet werden.
        IF OLD.produkt_nr IS DISTINCT FROM NEW.produkt_nr THEN
            UPDATE produkt
            SET durchschnittsbewertung = COALESCE((
                SELECT ROUND(AVG(punkte)::NUMERIC, 2)
                FROM rezension
                WHERE produkt_nr = OLD.produkt_nr
            ), 0)
            WHERE produkt_nr = OLD.produkt_nr;
        END IF;

        UPDATE produkt
        SET durchschnittsbewertung = COALESCE((
            SELECT ROUND(AVG(punkte)::NUMERIC, 2)
            FROM rezension
            WHERE produkt_nr = NEW.produkt_nr
        ), 0)
        WHERE produkt_nr = NEW.produkt_nr;

        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        UPDATE produkt
        SET durchschnittsbewertung = COALESCE((
            SELECT ROUND(AVG(punkte)::NUMERIC, 2)
            FROM rezension
            WHERE produkt_nr = OLD.produkt_nr
        ), 0)
        WHERE produkt_nr = OLD.produkt_nr;

        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_rezension_bewertung
AFTER INSERT OR UPDATE OR DELETE ON rezension
FOR EACH ROW
EXECUTE FUNCTION update_produkt_bewertung();

UPDATE produkt p
SET durchschnittsbewertung = COALESCE((
    SELECT ROUND(AVG(r.punkte)::NUMERIC, 2)
    FROM rezension r
    WHERE r.produkt_nr = p.produkt_nr
), 0);