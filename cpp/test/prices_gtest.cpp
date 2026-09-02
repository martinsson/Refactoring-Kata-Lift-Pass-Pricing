#include <atomic>
#include <condition_variable>
#include <mutex>
#include <thread>

#include <httplib.h>      /* https://gitee.com/c0ding/cpp-httplib */
#include <json.hpp>       /* https://github.com/nlohmann/json v3.10.5 */
#include <mysql_driver.h> /* https://dev.mysql.com/doc/dev/connector-cpp/8.0/ */
#include <cppconn/statement.h>

#include "Prices.h"
#include <gtest/gtest.h>

#define PORT 1234
#define CONTENT_TYPE "application/json"

class PricesTest : public testing::Test {
public:
    PricesTest() : cli("localhost", PORT)
    {
    }

private:
    static void runServer()
    {
        server->bind_to_port("localhost", PORT);
        notifyServerOpen();
        server->listen_after_bind();
        server_open = false;
    }

    static void notifyServerOpen()
    {
        std::lock_guard<std::mutex> server_lock(server_mutex);
        server_open = true;
        server_open_condition.notify_all();
    }

    static void waitForServerOpen()
    {
        std::unique_lock<std::mutex> server_lock(server_mutex);
        std::function<bool()> predicate = [](){ return server_open.load(); };
        server_open_condition.wait_for(server_lock, std::chrono::seconds(2), predicate);
        ASSERT_TRUE(server_open);
    }

protected:
    static void SetUpTestSuite()
    {
        sql::mysql::MySQL_Driver* driver;
        driver = sql::mysql::get_mysql_driver_instance();
        con = driver->connect("tcp://localhost:3306", "root", "mysql");
        con->setSchema("lift_pass");

        server = std::make_unique<httplib::Server>();
        Prices::createApp(*server, con);
        server_thread = std::thread(&PricesTest::runServer);
        waitForServerOpen();
    }

    static void TearDownTestSuite()
    {
        if (server != nullptr) {
            server->stop();
        }
        if (server_thread.joinable()) {
            server_thread.join();
        }
        server.reset();
        delete con;
    }

private:
    static std::unique_ptr<httplib::Server> server;
    static std::atomic_bool server_open;
    static std::condition_variable server_open_condition;
    static std::mutex server_mutex;
    static std::thread server_thread;

    httplib::Client cli;
    httplib::Headers headers = {std::make_pair("Accept", CONTENT_TYPE)};

protected:
    static sql::Connection* con;
    int cost = -1;
};

std::unique_ptr<httplib::Server> PricesTest::server;
std::atomic_bool PricesTest::server_open(false);
std::condition_variable PricesTest::server_open_condition;
std::mutex PricesTest::server_mutex;
std::thread PricesTest::server_thread;
sql::Connection* PricesTest::con;

TEST_F(PricesTest, DoesSomething)
{
    cost = -1;
    std::ostringstream path;
    // construct some proper url parameters
    path << "/prices?";

    auto response = cli.Get(path.str().c_str(), headers);
    ASSERT_TRUE(response != nullptr);
    EXPECT_EQ(response->status, 200);

    ASSERT_TRUE(response->has_header("Content-Type"));
    EXPECT_EQ(response->get_header_value("Content-Type"), CONTENT_TYPE);

    auto json = nlohmann::json::parse(response->body);
    cost = json["putSomethingHere"];
    EXPECT_EQ(cost, 35);
}
