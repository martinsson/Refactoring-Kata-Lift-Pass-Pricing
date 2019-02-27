#!/usr/bin/env bash

docker rm -f mariadb 2&> /dev/null
docker run -p 3306:3306 --name mariadb -e MYSQL_ROOT_PASSWORD=mysql -d  -v $PWD/database:/docker-entrypoint-initdb.d mariadb:10.4
#echo Waiting for mariadb to start
#while ! docker exec mariadb mysqladmin -pmysql --silent ping; do
#    sleep 1
#done
#echo mariadb succesfully started
#docker exec mariadb ls -la /var/run/mysqld/mysqld.sock
#sleep 2
#docker exec mariadb bash -c 'mysql -pmysql < /tmp/initDatabase.sql'
