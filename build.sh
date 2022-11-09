#!/bin/sh

mvn clean package

java -jar target/blogapp-1.0.0-jar-with-dependencies.jar
