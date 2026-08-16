SET search_path TO media_store;

-- Remove olf versions
DROP TRIGGER IF EXISTS trg_rezension_bewertung ON rezension;
DROP FUNCTION IF EXISTS update_produkt_rating();

-- Create function to update avg_rating and rating_quantity. 
CREATE OR REPLACE FUNCTION update_produkt_rating()
RETURNS TRIGGER AS $$
BEGIN
    -- On Insert in rezension table, calculate
    -- the avg_rating and rating_quantity on 
    -- the referenced product
    IF TG_OP = 'INSERT' THEN 
        UPDATE media_store.produkt
        SET avg_rating = COALESCE((
                SELECT ROUND(AVG(punkte)::NUMERIC, 2)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            ), NULL),
            rating_quantity = (
                SELECT COUNT(*)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            )
        WHERE produkt_nr = NEW.produkt_nr;

        RETURN NEW;

    -- On Update in rezension table, ...
    ELSIF TG_OP = 'UPDATE' THEN
        -- ... If the referenced product changed, 
        -- recalculate the avg_rating and rating_quantity
        -- on the old product, and ... 
        IF OLD.produkt_nr IS DISTINCT FROM NEW.produkt_nr THEN
            UPDATE media_store.produkt
            SET avg_rating = COALESCE((
                SELECT ROUND(AVG(punkte)::NUMERIC, 2)
                FROM media_store.rezension
                WHERE produkt_nr = OLD.produkt_nr
            ), NULL),
            rating_quantity = (
                SELECT COUNT(*)
                FROM media_store.rezension
                WHERE produkt_nr = OLD.produkt_nr
            )
            WHERE produkt_nr = OLD.produkt_nr;
        END IF;

        -- ... recalculate the avg_rating and rating_quantity
        -- on the referenced product
        UPDATE media_store.produkt
        SET avg_rating = COALESCE((
                SELECT ROUND(AVG(punkte)::NUMERIC, 2)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            ), NULL),
            rating_quantity = (
                SELECT COUNT(*)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            )
        WHERE produkt_nr = NEW.produkt_nr;

        RETURN NEW;

    -- On Delete on rezension table, recalculate
    -- avg_rating and rating_quantity on the 
    -- previously referenced product
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE media_store.produkt
        SET avg_rating = COALESCE((
                SELECT ROUND(AVG(punkte)::NUMERIC, 2)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            ), NULL),
            rating_quantity = (
                SELECT COUNT(*)
                FROM media_store.rezension
                WHERE produkt_nr = NEW.produkt_nr
            )
        WHERE produkt_nr = OLD.produkt_nr;

        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Set trigger to execute function
CREATE TRIGGER trg_rezension_bewertung
AFTER INSERT OR UPDATE OR DELETE ON media_store.rezension
FOR EACH ROW
EXECUTE FUNCTION update_produkt_rating();

-- Initial fill
UPDATE media_store.produkt p
SET avg_rating = COALESCE((
        SELECT ROUND(AVG(r.punkte)::NUMERIC, 2)
        FROM media_store.rezension r
        WHERE r.produkt_nr = p.produkt_nr
    ), NULL),
    rating_quantity = (
        SELECT COUNT(*)
        FROM media_store.rezension r
        WHERE r.produkt_nr = p.produkt_nr
    );