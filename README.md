# DBPraktikum Uni Leipzig 2026

To run this project, you should have maven and at least JDK 17 installed. 

First, start a PostgreSQL DBS instance. If you don't have one, you can easily start one using docker and docker compose by running 
```
docker compose up
```
from the project root directory. 
You can edit the connection details for connecting to another PostgreSQL DBS in loader/src/main/java/de/unileipzig/dbpraktikum/loader/db/DB.java, if required. 

To run the Loader, navigate to the "loader" directory and then execute with a path to an INPUT_FILE via
```
mvn exec:java -Dexec.mainClass=de.unileipzig.dbpraktikum.loader.MediaStoreLoader  -Dexec.args="../data/INPUT_FILE"
```

All errors will be reported to a file called "error-log.txt" in /loader. 