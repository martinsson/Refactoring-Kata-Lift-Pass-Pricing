

     docker run -p 3306:3306 --name mariadb -e  MYSQL_ROOT_PASSWORD=mysql -d mariadb:10.4 
     docker exec -it mariadb mysql -p 
     create database `test`;
     create table `test.liftpass`;
     CREATE TABLE IF NOT EXISTS test.liftpass (
         pass_id INT AUTO_INCREMENT,
         type VARCHAR(255) NOT NULL,
         cost INT NOT NULL,
         PRIMARY KEY (pass_id),
         UNIQUE KEY (type)
         );
         
