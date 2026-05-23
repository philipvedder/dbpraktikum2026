# Teil 2b: Integritätssicherung

Zur Wahrung der Konsistenz wird ein Trigger auf der Tabelle rezension verwendet.

Die durchschnittliche Bewertung eines Produkts wird im Attribut produkt.durchschnittsbewertung gespeichert. Nach jeder Einfüge-, Änderungs- oder Löschoperation auf der Tabelle rezension wird dieses Attribut automatisch neu berechnet.

Bei INSERT wird die Durchschnittsbewertung des neu bewerteten Produkts aktualisiert.
Bei UPDATE wird die Bewertung des betroffenen Produkts neu berechnet. Falls die Rezension einem anderen Produkt zugeordnet wird, werden sowohl das alte als auch das neue Produkt aktualisiert.
Bei DELETE wird die Durchschnittsbewertung des Produkts aktualisiert, zu dem die gelöschte Rezension gehörte.

Falls ein Produkt keine Rezensionen mehr besitzt, wird die durchschnittliche Bewertung auf 0 gesetzt.

Dadurch bleibt produkt.durchschnittsbewertung konsistent mit den vorhandenen Bewertungen in der Tabelle rezension.