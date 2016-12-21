#!/bin/bash

service bookshare stop
git pull
mvn package
service bookshare start

