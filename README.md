[<img src=https://user-images.githubusercontent.com/6883670/31999264-976dfb86-b98a-11e7-9432-0316345a72ea.png height=75 />](https://reactome.org)

# Interactors Core


### Introduction

This project parses IntAct clustered interactions and save them into a lightweight database (SQLite). Once the database is created, the graph-importer will consume it and then interactor will be part of the Graph Database.


### Clone and Package

- Clone project

```console
git clone https://github.com/reactome-pwp/interactors-core.git
cd interactors-core
# Note: You must skip test because at this point the database hasn't been created yet.
mvn clean package -DskipTests
```

### Main: IntactParser.java

#### Parameters:
* ````-d```` download the latest version 'intact-micluster.txt' from IntAct (see -t and -u)
* ````-f```` file to be parsed (cannot be used if -d is specified)
* ````-g```` database file (must be unique in the folder)
* ````-t```` folder to save the file when downloaded
* ````-u```` specify another ftp URL to download the file from IntAct.


#### Recommend:

```console
java -jar target/InteractorsParser-exec.jar -g /path/to/interactors.db -d
```

### Validate 

* To validate the database creation the tests can be performed.

```console
mvn package -Dinteractors.SQLite=interactors.db
```

### Extras

* Logs: The logs are stored in logs/ directory
* parser-messages.txt: This file stores all the parser messages. Any oddity will be written on it.
