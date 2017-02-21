<img src=https://cloud.githubusercontent.com/assets/6883670/22938783/bbef4474-f2d4-11e6-92a5-07c1a6964491.png width=220 height=100 />

# Interactors Core


### Introduction

This project parses IntAct clustered interactions and save them into a lightweight database (SQLite). Once the database is created, the Service layer can be used to access interactor data.


### Clone and Package

- Clone project

```console
git clone https://github.com/reactome-pwp/interactors-core.git
cd interactors-core
# Note: You must skip test because at this point the database hasn't been created yet.
mvn package -DskipTests
```

### How to run the parser in order to create the database ?

- IntActParser CLI

```console
java -jar target/InteractorsParser-jar-with-dependencies.jar
  -f <IntAct file to be parsed>
  -g <Interactor Database Path> ** Required **
  -d <Flag to download IntAct file>
  -t <Folder to save the downloaded file>
```

* Running IntactParser and download (-d) `intact-micluster.txt` (Recommended)

```console
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -d
```

or

* intact-micluster.txt is going to be saved in /tmp by default. Specify -t <path> and change the destination folder.

```console
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -d -t download
```

or

* Instead of downloading intact-micluster.txt every execution, using -f is possible to point to an existing file

```console
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -f download/intact-micluster.txt
```

### Validate 

* To validate the database creation the tests can be performed.

```console
mvn package -Dinteractors.SQLite=interactors.db
```

### Extras

* Logs: The logs are stored in logs/ directory
* parser-messages.txt: This file stores all the parser messages. Any oddity is written on it.
