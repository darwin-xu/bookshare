#!/bin/bash

service bookshared stop
git pull
mvn package -Dmaven.test.skip=true
service bookshared start
