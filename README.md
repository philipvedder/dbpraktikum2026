# DBPraktikum Uni Leipzig 2026

## Running the Loader

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

## Architecture

Current Pipeline is as follows:
1. The MediaStoreLoader is the entry point. Depending on the File argument, it starts the pipeline with calling a InputReader.
2. CSVReader or XMLReader reads the input file and builds a simple type from that, which allows conveniently reading the file data. 
For CSV this is a simple List of CSV rows, where each row is a List of Strings. For XML this is a XMLElement object. 
3. Then the corresponding parser is called, which build 'RawObjects' from the file content. A 'RawObject' is a object for a data type which already has the correct structure, but holds all fields as String variables, instead of correct types. 
4. After, the corresponding Validator takes the RawObjects as input, validates the content of the field for type, format, reasonability, integrity, etc. and converts to a correctly-typed object. 
5. The corresponding ImportService is called, which will call all needed Repositories to insert the new information into the DB. 
6. The Repositories containt the actual SQL and interact with a DB Connection object. 

Some other files/directories are:
DB.java: handles the DB connection, and open a DB Connection object. 
DOMUtil.java: helps with the interaction with XMLElement objects. 
ErrorLogger.java: Can take a list of ValidatorExceptions, and print their meaning to the error-log.txt file. Also is able print print a summary. 
exceptions: contains all ValidatorExceptions for all different checks in the Validators and ImportServices. 
