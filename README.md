A very simple sample management
===============================

This repository contains a very simple software project demonstration.
It contains of the following modules.

ProbenAPI
---------

This module contains a simple API. Some interfaces are provided by
means of abstract classes providing partial implementations.

ProbenVector
------------

This module contains an implementation of ProbenAPI which is based of
a simple in-memory representation of simple objects.

ProbenSQL
---------

This module contains a secound implementation of the API, based on
JDBC and the sqlite library. The DBMS related code is limited to
opening the database and table creation. So it can be easily adopted
to other JDBC backends.

ProbenTest
----------

This is a simple console application that allows to manage samples. It is 
driven by a simple command language that mainly resembles the API.

A summary of the commands and the teir syntax can be printed with the command `help`:

```
$ java -jar ProbenTest/target/ProbenTest-1.0.0-SNAPSHOT-bin.jar jdbc:sqlite::memory: test
help
help
remove   id [date]
quit
# [this line will be output verbatim]
range    from to
add      [id] date [number]
result   GOOD|FUZZY|BAD
list
addvalue id [date] value
$
```

or with sqlite:

```
e$ java -jar ProbenTest/target/ProbenTest-1.0.0-SNAPSHOT-bin.jar
# in-Memory solution
help
help
# [this line will be output verbatim]
range    from to
add      [id] date [number]
result   GOOD|FUZZY|BAD
list
addvalue id [date] value
remove   id [date]
quit
quit
# exiting...
$
```


Build instructions
------------------

On the command line just type:

```
mvn package
```


Then everything should be downloaded, installed and built. The build
includes JavaDoc generated webpages and JaCoCo based coverage reports.

All generated files are located inside of the target folder in each
module directory.
