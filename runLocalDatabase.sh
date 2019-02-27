#!/usr/bin/env bash

docker rm -f mariadb 2&> /dev/null
docker run -p 3306:3306 --name mariadb -e MYSQL_ROOT_PASSWORD=mysql -d  -v $PWD/database:/docker-entrypoint-initdb.d mariadb:10.4
