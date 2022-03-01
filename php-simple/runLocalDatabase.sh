#!/usr/bin/env bash

docker rm -f mariadb 2&> /dev/null

# start mariadb and launch all sql scripts in the directory database
# doit être lancé dans le repertoire java pour que $PWD/database pointe sur le repertoire où se trouve le script sql
cd "$(dirname "$0")"

docker run -p 3306:3306 --name mariadb -e MYSQL_ROOT_PASSWORD=mysql -d  -v $PWD/database:/docker-entrypoint-initdb.d mariadb:10.4
