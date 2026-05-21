# DBPraktikum Uni Leipzig 2026

To run this project, you should have maven and at least JDK 17 installed. 

Navigate to the "loader" directtory and then execute with a path to an INPUT_FILE via
```
mvn exec:java -Dexec.mainClass=de.unileipzig.dbpraktikum.loader.MediaStoreLoader  -Dexec.args="../data/INPUT_FILE"
```
