#!/bin/bash
mvn clean
mvn package -Dmaven.test.skip=true  
docker build . -t log-collector
