## Entity-Klassen:
- Produkt
- Buch
- MusikCd
- Dvd
- Person
- Verlag
- MusikCdTitel
- Kategorie
- Filiale
- Angebot
- Kunde
- Bestellung
- Bestellposition
- Rezension
- DvdBeteiligung

## Beziehungstabellen:
- buch_autor
- musik_cd_kuenstler
- produkt_kategorie
- aehnliches_produkt


## Interface-Entwurf

```java
import java.util.List;
import java.util.Properties;

public interface MediaStoreService {
/**
 * initialisiert die datenbankverbindung.
 */
void init(Properties properties);
void finish();
/**
 * gibt ein produkt anhand der produktnummer zurück
 */
Produkt getProduct(Long produktNr);
/**
 * sucht produkte nach einem titel-pattern
 */
List<Produkt> getProducts(String pattern);
/**
 * gibt den kompletten kategoriebaum zurück
 */
Kategorie getCategoryTree();
/**
 * gibt produkte für einen bestimmten kategoriepfad zurück
 */
List<Produkt> getProductsByCategoryPath(List<String> categoryPath);
/**
 * gibt die besten k produkte nach rating zurück
 */
List<Produkt> getTopProducts(int k);
/**
 * sucht ähnliche produkte, die günstiger sind
 */
List<Produkt> getSimilarCheaperProduct(Long produktNr);
/**
 * fügt eine neue rezension hinzu
 */
Rezension addNewReview(Long produktNr, Long kundeId, int punkte, String rezensionstext);
/**
 * gibt kunden mit niedriger durchschnittsbewertung zurück
 */
List<Kunde> getTrolls(double maxRating);
/**
 * gibt alle angebote für ein produkt zurück
 */
List<Angebot> getOffers(Long produktNr);

}
```
