#!/bin/bash

service bookshare stop
git pull
mvn package -Dmaven.test.skip=true
service bookshare start
