# Interactors Core


### Introduction

This project parses IntAct clustered interactions and save them into a lightweight database (SQLite). Once the database is created, the Service layer can be used to access interactor data.


### Clone and Package

- Clone project

```
git clone https://github.com/reactome-pwp/interactors-core.git
```

- Go to interactors-core directory and package. 
Note: You must skip test because at this point the database hasn't been created yet.

```
mvn package -DskipTests
```

### How to run the parser in order to create the database ?

- IntActParser CLI

```
java -jar target/InteractorsParser-jar-with-dependencies.jar
  -f <IntAct file to be parsed>
  -g <Interactor Database Path> ** Required **
  -d <Flag to download IntAct file>
  -t <Folder to save the downloaded file>
```

* Running IntactParser and download (-d) intact-micluster.txt (Recommended)

```
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -d
```

or

* intact-micluster.txt is going to be saved in /tmp by default. Specify -t <path> and change the destination folder.

```
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -d -t download
```

or

* Instead of downloading intact-micluster.txt every execution, using -f is possible to point to an existing file

```
java -jar target/InteractorsParser-jar-with-dependencies.jar -g interactors.db -f download/intact-micluster.txt
```

### Validate 

* To validate the database creation the tests can be performed.

```
mvn package -Dinteractors.SQLite=interactors.db
```

### Extras

* Logs: The logs are stored in logs/ directory
* parser-messages.txt: This file stores all the parser messages. Any oddity is written on it.
