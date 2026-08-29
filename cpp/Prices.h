#ifndef PRICES_H
#define PRICES_H

#include <httplib.h>          /* https://gitee.com/c0ding/cpp-httplib */
#include <mysql_connection.h> /* https://dev.mysql.com/doc/dev/connector-cpp/8.0/ */

class Prices {
public:
    static void createApp(httplib::Server& server, sql::Connection* con);
};

#endif // PRICES_H
