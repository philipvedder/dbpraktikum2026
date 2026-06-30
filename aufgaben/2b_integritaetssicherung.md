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