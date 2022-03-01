# Install
Run

    composer install

# Setup database
The simplest solution is to use a docker container with mysql. You just need docker
then you can run the file `./runLocalDatabase.sh` which will download the image, start it and inject the data in `database/initDatabase.sql`.
Beware to launch it in this directory.

# Run application

    php -S localhost:8000 -t bootstrap
    
[try it in the browser](http://localhost:8000/prices/?type=1jour&age=10)

# Start testing

Head over to PricesControllerTest, run it and simply add some more tests

# Framework 
The framework used for this kata is [Lumen](https://lumen.laravel.com/docs/7.x)
