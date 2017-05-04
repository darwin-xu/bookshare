#!/bin/bash

java -Dspring.profiles.active=production -jar /root/code/bookshare/server/target/bookshare-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
echo $! > /var/run/bookshare/bookshare.pid

