# Lift Pass Pricing C++ API

Lift Pass Pricing Refactoring Kata.

This is a [CMake](https://cmake.org/) project.

MySQL client bindings 8.x have to be installed. Use the [MySQL Connector/C++](https://dev.mysql.com/downloads/connector/cpp/).
If you download the executable installer, make sure to select full install. Other install variants do not set up development components.
In the `CMakeLists.txt` you need to set the `MYSQL_HOME_DIR` variable accordingly. (Maybe you need to update `MYSQL_LIBRARY_DIR` and the four declared libraries, too.)
When using Windows, the MySQL Connector needs Visual Studio and does not support GCC.
On Linux you try `apt-get install libmysqlcppconn-dev` to get the required files.

To build:

    mkdir build
    cd build
    cmake -G "Unix Makefiles" ..
    cmake --build .

or to build on Windows:

    mkdir build
    cd build
    cmake -G "Visual Studio 17 2022" ..
    cmake --build .

To run the tests:

    ctest

To start the application:

    ./lift-pass
