#include "Prices.h"
#include <iostream>
#include <mysql_driver.h>

int main(void)
{
    try {
        httplib::Server server;

        sql::mysql::MySQL_Driver* driver;
        driver = sql::mysql::get_mysql_driver_instance();
        
        sql::Connection* con;
        con = driver->connect("tcp://localhost:3306", "root", "mysql");
        con->setSchema("lift_pass");

        Prices::createApp(server, con);

        std::cout << "LiftPassPricing Api started on 1234,\n"
                    "you can open http://localhost:1234/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                    "and you'll get the price of the list pass for the day."
                << std::endl;
        server.listen("localhost", 1234); // waits
        
        delete con;
        
    } catch (sql::SQLException &e) {
        std::cerr << "SQL Exception: " << e.what() << std::endl;
    }

    return EXIT_SUCCESS;
}
