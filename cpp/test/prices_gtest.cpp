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

    void createPrice(std::string const &type, int cost)
    {
        std::ostringstream path;
        path << "/prices?";
        path << "type" << "=" << type;
        path << "&" << "cost" << "=" << cost;

        auto response = cli.Put(path.str().c_str(), headers, "{}", CONTENT_TYPE);
        ASSERT_TRUE(response != nullptr);
        EXPECT_EQ(response->status, 200); // TODO should be 204

        ASSERT_TRUE(response->has_header("Content-Type"));
        EXPECT_EQ(response->get_header_value("Content-Type"), CONTENT_TYPE);
    }

    void obtainPrice(std::string const &parameters)
    {
        cost = -1;
        std::ostringstream path;
        path << "/prices?" << parameters;

        auto response = cli.Get(path.str().c_str(), headers);
        ASSERT_TRUE(response != nullptr);
        EXPECT_EQ(response->status, 200);

        ASSERT_TRUE(response->has_header("Content-Type"));
        EXPECT_EQ(response->get_header_value("Content-Type"), CONTENT_TYPE);

        auto json = nlohmann::json::parse(response->body);
        cost = json["cost"];
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

TEST_F(PricesTest, UpdateDefaultPriceDay)
{
    createPrice("1jour", 35);
}
TEST_F(PricesTest, UpdateDefaultPriceNight)
{
    createPrice("night", 19);
}

TEST_F(PricesTest, CreateNewPrice)
{
    createPrice("summer", 15);

    std::string sql = "delete from lift_pass.base_price "
                        "where lift_pass.base_price.type = 'summer'";
    sql::Statement* stmt = con->createStatement();
    int rows = stmt->executeUpdate(sql);
    con->commit();
    delete stmt;

    EXPECT_EQ(rows , 1);
}

TEST_F(PricesTest, DefaultCost)
{
    obtainPrice("type=1jour");
    EXPECT_EQ(cost, 35);
}

TEST_F(PricesTest, CostForAge5)
{
    obtainPrice("type=1jour&age=5");
    EXPECT_EQ(cost, 0);
}
TEST_F(PricesTest, CostForAge6)
{
    obtainPrice("type=1jour&age=6");
    EXPECT_EQ(cost, 25);
}
TEST_F(PricesTest, CostForAge14)
{
    obtainPrice("type=1jour&age=14");
    EXPECT_EQ(cost, 25);
}
TEST_F(PricesTest, CostForAge15)
{
    obtainPrice("type=1jour&age=15");
    EXPECT_EQ(cost, 35);
}
TEST_F(PricesTest, CostForAge25)
{
    obtainPrice("type=1jour&age=25");
    EXPECT_EQ(cost, 35);
}
TEST_F(PricesTest, CostForAge64)
{
    obtainPrice("type=1jour&age=64");
    EXPECT_EQ(cost, 35);
}
TEST_F(PricesTest, CostForAge65)
{
    obtainPrice("type=1jour&age=65");
    EXPECT_EQ(cost, 27);
}

TEST_F(PricesTest, RealNightCost)
{
    obtainPrice("type=night");
    EXPECT_EQ(cost, 0);
}

TEST_F(PricesTest, DefaultNightCost)
{
    GTEST_SKIP();

    obtainPrice("type=night");
    EXPECT_EQ(cost, 19);
}

TEST_F(PricesTest, CostForNightPassAge5)
{
    obtainPrice("type=night&age=5");
    EXPECT_EQ(cost, 0);
}
TEST_F(PricesTest, CostForNightPassAge6)
{
    obtainPrice("type=night&age=6");
    EXPECT_EQ(cost, 19);
}
TEST_F(PricesTest, CostForNightPassAge25)
{
    obtainPrice("type=night&age=25");
    EXPECT_EQ(cost, 19);
}
TEST_F(PricesTest, CostForNightPassAge64)
{
    obtainPrice("type=night&age=64");
    EXPECT_EQ(cost, 19);
}
TEST_F(PricesTest, CostForNightPassAge65)
{
    obtainPrice("type=night&age=65");
    EXPECT_EQ(cost, 8);
}

TEST_F(PricesTest, CostForForMondayDealFriday)
{
    obtainPrice("type=1jour&age=15&date=2019-02-22");
    EXPECT_EQ(cost, 35);
}
TEST_F(PricesTest, CostForForMondayDealHoliday)
{
    obtainPrice("type=1jour&age=15&date=2019-02-25");
    EXPECT_EQ(cost, 35);
}
TEST_F(PricesTest, CostForForMondayDealAge15)
{
    obtainPrice("type=1jour&age=15&date=2019-03-11");
    EXPECT_EQ(cost, 23);
}
TEST_F(PricesTest, CostForForMondayDealAge65)
{
    obtainPrice("type=1jour&age=65&date=2019-03-11");
    EXPECT_EQ(cost, 18);
}

// TODO 2-4, and 5, 6 day pass
