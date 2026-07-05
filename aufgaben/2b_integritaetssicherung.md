# Teil 2b: Integritätssicherung

Zur Wahrung der Konsistenz wird ein Trigger auf der Tabelle rezension verwendet.

Die durchschnittliche Bewertung eines Produkts wird im Attribut produkt.avg_rating gespeichert. Außerdem wird die Gesamtanzahl an Bewertungen für ein Produkt in produkt.rating_quantity gespeichert. 
Standardmäßig ist avg_rating = NULL und rating_quantity = 0 (Z.B. nach einfügen eines neuen Produktes)

Nach jeder Einfüge-, Änderungs- oder Löschoperation auf der Tabelle rezension werden diese Attribute automatisch neu berechnet.

Bei INSERT wird die Durchschnittsbewertung und Anzahl des neu bewerteten Produkts aktualisiert.
Bei UPDATE wird die Bewertung und Anzahl des betroffenen Produkts neu berechnet. Falls die Rezension einem anderen Produkt zugeordnet wird, werden sowohl das alte als auch das neue Produkt aktualisiert.
Bei DELETE wird die Durchschnittsbewertung und Anzahl des Produkts aktualisiert, zu dem die gelöschte Rezension gehörte.

Falls ein Produkt keine Rezensionen mehr besitzt, wird die durchschnittliche Bewertung auf NULL gesetzt.
Die Gesamtanzahl ist dann entsprechend 0.

Der Trigger kann z.b. mit dem folgenden SQL skript gestestet werden. Zwischen den zwei Ergebnistabellen ändert sich das avg_rating des Produktes.
```sql
update rezension
set punkte = 5
where rezension.rezension_id = 10154;

select * from produkt p
left join rezension r
	on r.produkt_nr = p.produkt_nr 
where p.produkt_nr = '345806785X';

update rezension
set punkte = 1
where rezension.rezension_id = 10154;

select * from produkt p
left join rezension r
	on r.produkt_nr = p.produkt_nr 
where p.produkt_nr = '345806785X';
```
