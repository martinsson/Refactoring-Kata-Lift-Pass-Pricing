
# Lift pass pricing

This is a refactoring kata, where you could learn how to take on some 
non unit-testable code that integrates with a database and with some rest-based 
framework. This code cannot be unit tested, therefor it models a 
common problem - code that isn't designed to be tested. You'll have
tu put in place some black-box tests integrating with the database and possibly 
accessing the http api. Once the code is covered you can refactor it in order
to make some kind of architecture appear (layered, hexagonal, onion, ...). 
Finally once the logic is not exclusively in the controller, you might be able
to unit test it. 

# When am I done?
Once the testing pyramid is respected, i.e. tests should be stable and fast. 
There should be a lot more unit tests than tests on the rest api and tests 
integrating with the database.

# Installation  
Set up a database. For instance by executing this in a terminal:

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
         
Then head on to the language of your choice and follow the Readme in there.
         
