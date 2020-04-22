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


Build instructions
------------------

On the command line just type:

``` sh
mvn package
```


Then everything should be downloaded, installed and built. The build
includes JavaDoc generated webpages and JaCoCo based coverage reports.

All generated files are located inside of the target folder in each
module directory.
