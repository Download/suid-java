# suid-java
### Java implementation of Scoped Unique IDs

**Suids are distributed, Scoped, Unique IDs that are short and sweet.**

> See the main [project](https://download.github.io/suid/) for details.

[![version](https://maven-badges.herokuapp.com/maven-central/ws.suid/suid-java/badge.png)](https://search.maven.org/#artifactdetails%7Cws.suid%7Csuid-java%7C0.12.0%7Cjar)
[![license](https://img.shields.io/badge/license-CC--BY--4.0-blue.svg)](https://creativecommons.org/licenses/by/4.0/)
[![build status](https://api.travis-ci.org/Download/suid-java.svg?branch=master)](https://travis-ci.org/Download/suid-java)
![mind BLOWN](https://img.shields.io/badge/mind-BLOWN-ff69b4.svg)


## Download
* [suid-java-0.12.0.jar](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0.jar) ([signature](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0.jar.asc))
* [suid-java-0.12.0-sources.jar](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0-sources.jar) ([signature](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0-sources.jar.asc))
* [suid-java-0.12.0-javadoc.jar](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0-javadoc.jar) ([signature](http://search.maven.org/remotecontent?filepath=ws/suid/suid-java/0.12.0/suid-java-0.12.0-javadoc.jar.asc))

## Maven coordinates:
```xml
<dependency>
	<groupId>ws.suid</groupId>
	<artifactId>suid-java</artifactId>
	<version>0.12.0</version>
</dependency>
```

## Import
```java
import ws.suid.Suid;
```

## Usage
* [Create a Suid from a long](#create-a-suid-from-a-long)
* [Create a Suid from a string](#create-a-suid-from-a-string)
* [Get a Suid's underlying value](#get-a-suid-s-underlying-value)
* [Convert a Suid to a Long](#convert-a-suid-to-a-long)
* [Convert a Suid to a String](#convert-a-suid-to-a-string)
* [Convert a Suid list to a Long list](#convert-a-suid-list-to-a-long-list)
* [Convert a Suid list to a String List](#convert-a-suid-list-to-a-string-list)
* [Convert a Long list to a Suid list](#convert-a-long-list-to-a-suid-list)
* [Convert a String list to a Suid list](#convert-a-string-list-to-a-suid-list)

### Create a Suid from a long
```java
Suid id = new Suid(1903154L);
System.out.println(id); // 14she
```

### Create a Suid from a string
```java
Suid id = new Suid("14she");
System.out.println(id); // 14she
```

### Get a Suid's underlying value
```java
Suid id = new Suid("14she");
System.out.println(id.longValue()); // 1903154
```

### Convert a Suid to a Long
```java
Suid id = new Suid("14she");
Long val = id.toLong();
System.out.println(val); // 1903154
```

### Convert a Suid to a String
```java
Suid id = new Suid("14she");
String val = id.toString();
System.out.println(val); // 14she
```

### Convert a Suid list to a Long list
```java
List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
System.out.println(ids);  // [14she, 14shf, 14shg]
List<Long> vals = Suid.toLong(ids);
System.out.println(vals); // [1903154, 1903155, 1903156]
```

### Convert a Suid list to a String list
```java
List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
System.out.println(ids);  // [14she, 14shf, 14shg]
List<String> vals = Suid.toString(ids);
System.out.println(vals); // [14she, 14shf, 14shg]
```

### Convert a Long list to a Suid list
```java
List<Long> vals = Arrays.asList(new Long[]{Long.valueOf(1903154), Long.valueOf(1903155), Long.valueOf(1903156)});
System.out.println(vals); // [1903154, 1903155, 1903156]
List<Suid> ids = Suid.fromLong(vals);
System.out.println(ids);  // [14she, 14shf, 14shg]
```

### Convert a String list to a Suid list
```java
List<String> vals = Arrays.asList(new String[]{"14she", "14shf", "14shg"});
System.out.println(vals); // [14she, 14shf, 14shg]
List<Suid> ids = Suid.fromString(vals);
System.out.println(ids);  // [14she, 14shf, 14shg]
```

## Copyright
Copyright (c) 2017 by [Stijn de Witt](http://stijndewitt.com). Some rights reserved.

## License
Creative Commons Attribution 4.0 International (CC BY 4.0)
https://creativecommons.org/licenses/by/4.0/

