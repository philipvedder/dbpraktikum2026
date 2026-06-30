## 1. Anzahl der Produkte pro Typ

```sql
SELECT 
    COUNT(*) FILTER (WHERE produkttyp = 'BOOK') AS anzahl_buecher, 
    COUNT(*) FILTER (WHERE produkttyp = 'MUSIC_CD') AS anzahl_musik_cds, 
    COUNT(*) FILTER (WHERE produkttyp = 'DVD') AS anzahl_dvds 
FROM produkt;
```

**Ergebnis:**
"anzahl_buecher","anzahl_musik_cds","anzahl_dvds"
222,792,475
    
---

## 2. Die 5 besten Produkte jedes Typs sortiert nach Rating

```sql
WITH produkt_ratings AS (
    SELECT 
        produkttyp AS typ,
        produkt.produkt_nr AS produktnr,
        AVG(rezension.punkte) as avg_rating,
        COUNT(rezension.rezension_id) as quantity
    FROM produkt
    LEFT JOIN rezension
    	ON rezension.produkt_nr = produkt.produkt_nr 
    group by 
    	produkt.produkttyp,
    	produkt.produkt_nr 
),
ranked_produkte AS (
	SELECT 
		typ,
		produktnr,
		avg_rating,
		quantity,
		ROW_NUMBER() OVER (
			PARTITION BY typ
			ORDER BY avg_rating DESC NULLS LAST, quantity DESC, produktnr
		) AS rank
	FROM produkt_ratings
)
SELECT 
    typ,
    produktnr,
    avg_rating,
    rank,
    quantity
FROM ranked_produkte
WHERE rank <= 5
ORDER BY 
    typ,
    avg_rating DESC NULLS LAST,
    produktnr;
```
Wir geben nur die ersten 5 Produkte aus. Wir sortieren nach Durchschnittsbewertung, Anzhal Bewertungen, ProduktNr.

**Ergebnis:**
"typ","produktnr","avg_rating","rank","quantity"
BOOK,"3401053698",5.0000000000000000,1,5
BOOK,"3405168643",5.0000000000000000,2,5
BOOK,"3407784570",5.0000000000000000,3,5
BOOK,"343103196X",5.0000000000000000,4,5
BOOK,"3431036341",5.0000000000000000,5,5
MUSIC_CD,B0000007QD,5.0000000000000000,1,5
MUSIC_CD,B000006YMN,5.0000000000000000,2,5
MUSIC_CD,B00000DG17,5.0000000000000000,3,5
MUSIC_CD,B00000IGPN,5.0000000000000000,4,5
MUSIC_CD,B00000JAD4,5.0000000000000000,5,5
DVD,"6304498977",5.0000000000000000,1,5
DVD,"630463949X",5.0000000000000000,2,5
DVD,B00002ZMNV,5.0000000000000000,3,5
DVD,B00004RJEG,5.0000000000000000,4,5
DVD,B00004RYTK,5.0000000000000000,5,5

---

## 3. Produkte ohne aktuelles Angebot

```sql
SELECT 
	p.produkt_nr
FROM produkt p
LEFT JOIN angebot
	ON angebot.produkt_nr = p.produkt_nr
GROUP BY p.produkt_nr 	
HAVING COUNT(angebot.produkt_nr) = 0 
ORDER BY p.produkt_nr 
```

**Ergebnis:**
"produkt_nr"
"3110181460"
"3134843080"
(...)

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
"produkt_nr"
B00004CWTY

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
"produkt_nr"
"3401058371"
"3570016943"
(...)

---

## 6. Anzahl der Produkte ohne Rezension

```sql
SELECT COUNT(p.produkt_nr) AS produkte_ohne_rezensionen
FROM produkt p
LEFT JOIN rezension r 
	ON p.produkt_nr = r.produkt_nr
WHERE r.rezension_id IS NULL;
```

**Ergebnis:**
"produkte_ohne_rezensionen"
506

---

## 7. Rezensenten mit mindestens 10 geschriebenen Rezensionen

```sql
SELECT 
	k.kunde_id, 
	k.name, 
	COUNT(r.rezension_id) AS rezensionen_anzahl
FROM kunde k
JOIN rezension r 
	ON k.kunde_id = r.kunde_id
GROUP BY k.kunde_id
HAVING COUNT(r.rezension_id) >= 10
ORDER BY k.name, k.kunde_id;
```

**Ergebnis:**
"kunde_id","name","rezensionen_anzahl"
9,guest,385
85,petethemusicfan,10

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
"name"
Ac
Al
Dav
Nicole
Peter
Sandra

---

## 9. Durchschnittliche Anzahl von Liedern einer Musik-CD

```sql
SELECT 
	COALESCE(AVG(track_count), 0)::NUMERIC(10,2) AS durchschnittliche_anzahl_lieder
FROM (
    SELECT 
    	m.produkt_nr, 
    	COUNT(t.track_id) AS track_count
    FROM musik_cd m
    LEFT JOIN musik_cd_titel t 
    	ON m.produkt_nr = t.produkt_nr
    GROUP BY m.produkt_nr
) sub;
```

**Ergebnis:**
"durchschnittliche_anzahl_lieder"
20.60

---

## 10. Produkte mit ähnlichen Produkten in einer anderen Hauptkategorie

```sql
WITH RECURSIVE kategorie_tree AS (
    -- Hauptkategorien: Kategorien ohne Oberkategorie
    SELECT 
        k.kategorie_id,
        k.parent_kategorie_id,
        k.kategorie_id AS haupt_kategorie_id
    FROM kategorie k
    WHERE k.parent_kategorie_id IS NULL

    UNION all
    
    -- Unterkategorien erben die Hauptkategorie ihrer Oberkategorie
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
),
produkte_mit_aehnlichem_in_anderer_hauptkategorie AS (
    SELECT
        ph1.produkt_nr AS ph1_nr,
        ph2.produkt_nr AS ph2_nr,
        ph1.haupt_kategorie_id AS ph1_root_cat_id,
        ph2.haupt_kategorie_id AS ph2_root_cat_id
    FROM aehnliches_produkt ap
    JOIN produkt_hauptkategorie ph1
        ON ph1.produkt_nr = ap.produkt_nr_1
    JOIN produkt_hauptkategorie ph2
        ON ph2.produkt_nr = ap.produkt_nr_2
    WHERE ph1.haupt_kategorie_id <> ph2.haupt_kategorie_id 
    	AND ph1.produkt_nr <> ph2.produkt_nr
)
SELECT DISTINCT ph1_nr, ph2_nr
FROM produkte_mit_aehnlichem_in_anderer_hauptkategorie
```

**Ergebnis:**
"ph1_nr","ph2_nr"
B00004CY11,B0009JPQ56
B000654U46,B000066I6X
(...)

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
"produkt_nr"
B00004CWTY
B00004T8WB
(...)

---

## 12. Prozentsatz der Fälle aus Frage 11, in denen Leipzig das preiswerteste Angebot hat

```sql
WITH products_in_all_shops AS (
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
        MIN(a.preis) FILTER (WHERE f."name" ILIKE 'Leipzig') AS leipzig_preis
    FROM angebot a
    INNER JOIN filiale f 
    	ON a.filiale_id = f.filiale_id
    WHERE a.produkt_nr IN (
        SELECT produkt_nr 
        FROM products_in_all_shops
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
"leipzig_cheapest_percentage"
44.44

---

