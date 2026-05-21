# DBPraktikum Uni Leipzig 2026

To run this project, you should have maven and at least JDK 17 installed. 

Replace INPUT_FILE with the file you want to load and then execute from the root via
```
mvn exec:java -Dexec.mainClass=de.unileipzig.dbpraktikum.loader.MediaStoreLoader  -Dexec.args="data/INPUT_FILE"
```