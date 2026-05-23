## 1. Anzahl der Produkte pro Typ

```sql
SELECT 
    COUNT(*) FILTER (WHERE produkttyp = 'BUCH') AS anzahl_buecher, 
    COUNT(*) FILTER (WHERE produkttyp = 'MUSIK_CD') AS anzahl_musik_cds, 
    COUNT(*) FILTER (WHERE produkttyp = 'DVD') AS anzahl_dvds 
FROM produkt;
```

**Ergebnis:**

    
---

## 2. Die 5 besten Produkte jedes Typs sortiert nach Rating

```sql
WITH ranked_produkte AS (
    SELECT 
        produkttyp AS typ,
        produkt_nr AS produktnr,
        durchschnittsbewertung AS rating,
        DENSE_RANK() OVER (
            PARTITION BY produkttyp 
            ORDER BY durchschnittsbewertung DESC
        ) AS rang
    FROM produkt
)
SELECT typ, produktnr, rating
FROM ranked_produkte
WHERE rang <= 5
ORDER BY typ, rating DESC, produktnr;
```
Gleiche durchschnittliche Ratings erhalten denselben Rang.
Dadurch können bei Gleichstand mehr als fünf Produkte pro Typ ausgegeben werden.

**Ergebnis:**


---

## 3. Produkte ohne aktuelles Angebot

```sql
SELECT p.produkt_nr
FROM produkt p
WHERE NOT EXISTS (
    SELECT 1
    FROM angebot a
    WHERE a.produkt_nr = p.produkt_nr
      AND a.preis IS NOT NULL
)
ORDER BY p.produkt_nr;
```

**Ergebnis:**


---

## 4. Produkte, bei denen das teuerste Angebot mehr als doppelt so teuer ist wie das preiswerteste

```sql
SELECT produkt_nr
FROM angebot
WHERE preis IS NOT NULL
GROUP BY produkt_nr
HAVING MAX(preis) > 2 * MIN(preis)
ORDER BY produkt_nr;
```

**Ergebnis:**


---

## 5. Produkte mit sowohl einer sehr schlechten (1) als auch einer sehr guten (5) Bewertung

```sql
SELECT produkt_nr
FROM rezension
GROUP BY produkt_nr
HAVING MIN(punkte) = 1 AND MAX(punkte) = 5
ORDER BY produkt_nr;
```

**Ergebnis:**


---

## 6. Anzahl der Produkte ohne Rezension

```sql
SELECT COUNT(p.produkt_nr) AS produkte_ohne_rezensionen
FROM produkt p
LEFT JOIN rezension r ON p.produkt_nr = r.produkt_nr
WHERE r.rezension_id IS NULL;
```

**Ergebnis:**


---

## 7. Rezensenten mit mindestens 10 geschriebenen Rezensionen

```sql
SELECT k.kunde_id, k.name, COUNT(r.rezension_id) AS rezensionen_anzahl
FROM kunde k
JOIN rezension r ON k.kunde_id = r.kunde_id
GROUP BY k.kunde_id, k.name
HAVING COUNT(r.rezension_id) >= 10
ORDER BY k.name, k.kunde_id;
```

**Ergebnis:**


---

## 8. Buchautoren, die auch an DVDs oder Musik-CDs beteiligt sind

```sql
SELECT DISTINCT p.name
FROM person p
JOIN buch_autor ba ON p.person_id = ba.person_id
WHERE EXISTS (
    SELECT 1
    FROM dvd_beteiligung db
    WHERE db.person_id = p.person_id
)
OR EXISTS (
    SELECT 1
    FROM musik_cd_kuenstler mk
    WHERE mk.person_id = p.person_id
)
ORDER BY p.name;
```

**Ergebnis:**

---

## 9. Durchschnittliche Anzahl von Liedern einer Musik-CD

```sql
SELECT COALESCE(AVG(track_count), 0)::NUMERIC(10,2) AS durchschnittliche_anzahl_lieder
FROM (
    SELECT m.produkt_nr, COUNT(t.track_id) AS track_count
    FROM musik_cd m
    LEFT JOIN musik_cd_titel t ON m.produkt_nr = t.produkt_nr
    GROUP BY m.produkt_nr
) sub;
```

**Ergebnis:**

---

## 10. Produkte mit ähnlichen Produkten in einer anderen Hauptkategorie

```sql
WITH RECURSIVE kategorie_tree AS (
    SELECT 
        kategorie_id, 
        parent_kategorie_id, 
        kategorie_id AS haupt_kategorie_id
    FROM kategorie
    WHERE parent_kategorie_id IS NULL
    
    UNION ALL
    
    SELECT 
        k.kategorie_id, 
        k.parent_kategorie_id, 
        kt.haupt_kategorie_id
    FROM kategorie k
    JOIN kategorie_tree kt 
        ON k.parent_kategorie_id = kt.kategorie_id
),
produkt_hauptkategorie AS (
    SELECT DISTINCT 
        pk.produkt_nr, 
        kt.haupt_kategorie_id
    FROM produkt_kategorie pk
    JOIN kategorie_tree kt 
        ON pk.kategorie_id = kt.kategorie_id
)
SELECT DISTINCT p1.produkt_nr AS produkt_nr
FROM aehnliches_produkt ap
JOIN produkt_hauptkategorie p1 
    ON ap.produkt_nr_1 = p1.produkt_nr
JOIN produkt_hauptkategorie p2 
    ON ap.produkt_nr_2 = p2.produkt_nr
WHERE p1.haupt_kategorie_id <> p2.haupt_kategorie_id

UNION

SELECT DISTINCT p2.produkt_nr AS produkt_nr
FROM aehnliches_produkt ap
JOIN produkt_hauptkategorie p1 
    ON ap.produkt_nr_1 = p1.produkt_nr
JOIN produkt_hauptkategorie p2 
    ON ap.produkt_nr_2 = p2.produkt_nr
WHERE p1.haupt_kategorie_id <> p2.haupt_kategorie_id

ORDER BY produkt_nr;
```

**Ergebnis:**


## 11. Produkte, die in allen Filialen angeboten werden

```sql
SELECT produkt_nr
FROM angebot
WHERE preis IS NOT NULL
GROUP BY produkt_nr
HAVING COUNT(DISTINCT filiale_id) = (SELECT COUNT(*) FROM filiale)
ORDER BY produkt_nr;
```
**Ergebnis:**

---

## 12. Prozentsatz der Fälle aus Frage 11, in denen Leipzig das preiswerteste Angebot hat

```sql
WITH all_branches_products AS (
    SELECT produkt_nr
    FROM angebot
    WHERE preis IS NOT NULL
    GROUP BY produkt_nr
    HAVING COUNT(DISTINCT filiale_id) = (SELECT COUNT(*) FROM filiale)
),
cheapest_offers AS (
    SELECT 
        a.produkt_nr,
        MIN(a.preis) AS min_preis,
        MIN(a.preis) FILTER (WHERE f.ort ILIKE 'Leipzig') AS leipzig_preis
    FROM angebot a
    JOIN filiale f ON a.filiale_id = f.filiale_id
    WHERE a.produkt_nr IN (
        SELECT produkt_nr 
        FROM all_branches_products
    )
    AND a.preis IS NOT NULL
    GROUP BY a.produkt_nr
)
SELECT 
    (
        COUNT(*) FILTER (WHERE leipzig_preis = min_preis) 
        * 100.0 
        / NULLIF(COUNT(*), 0)
    )::NUMERIC(5,2) AS leipzig_cheapest_percentage
FROM cheapest_offers;
```
**Ergebnis:**

---
