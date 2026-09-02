#include "Prices.h"
#include <cppconn/resultset.h>
#include <cppconn/statement.h>
#include <iomanip>
#include <string>

#define QUERY_PARAMS(type) (*(req.params.find((type)))).second.c_str()

void Prices::createApp(httplib::Server& server, sql::Connection* con)
{

    server.Put("/prices", [con](const httplib::Request& req, httplib::Response& res) {
        int liftPassCost = std::stoi(QUERY_PARAMS("cost"));
        std::string liftPassType = QUERY_PARAMS("type");

        std::string query = "INSERT INTO lift_pass.base_price (type, cost) "
            "VALUES ('"+ liftPassType + "', " + std::to_string(liftPassCost) + ") "
            "ON DUPLICATE KEY UPDATE cost = " + std::to_string(liftPassCost);

        sql::Statement* stmt = con->createStatement();
        stmt->execute(query);
        con->commit();
        stmt->close();
        delete stmt;

        res.set_content("", "application/json");
    });

    server.Get("/prices", [con](const httplib::Request& req, httplib::Response& res) {
        int age = -1;
        auto it = req.params.find("age");
        if (it != req.params.end()) {
            age = std::stoi((*it).second.c_str());
        }

        std::string liftPassType = QUERY_PARAMS("type");
        std::string query = "SELECT cost FROM lift_pass.base_price "
                            "WHERE type = '" + liftPassType + "'";
        sql::Statement* stmt = con->createStatement();
        sql::ResultSet* rset = stmt->executeQuery(query);
        rset->next();
        int result = rset->getInt("cost");
        delete rset;
        delete stmt;

        int reduction;
        bool isHoliday = false;

        if (age != -1 && age < 6) {
            res.set_content("{ \"cost\": 0}", "application/json");
        }
        else {
            reduction = 0;

            if (strcmp(QUERY_PARAMS("type"), "night") != 0) {
                std::string isoFormat = "%Y-%m-%d";

                std::string query = "SELECT holiday FROM lift_pass.holidays";
                sql::Statement* holidayStmt = con->createStatement();
                sql::ResultSet* holidays = holidayStmt->executeQuery(query);
                while (holidays->next()) {
                    std::string holiday = holidays->getString("holiday");

                    it = req.params.find("date");
                    if (it != req.params.end()) {
                        std::string date = (*it).second.c_str();
                        if (holiday == date) {
                            isHoliday = true;
                        }
                    }
                }
                holidays->close();
                delete holidays;
                holidayStmt->close();
                delete holidayStmt;

                it = req.params.find("date");
                if (it != req.params.end()) {
                    std::tm d = {};
                    std::istringstream ss((*it).second.c_str());
                    ss >> std::get_time(&d, isoFormat.c_str());
                    std::mktime(&d);
                    if (!isHoliday && d.tm_wday == 1) {
                        reduction = 35;
                    }
                }

                /* TODO apply reduction for others */
                if (age != -1 && age < 15) {
                    res.set_content("{ \"cost\": " + std::to_string((int)std::ceil(result * .7)) + "}", "application/json");
                }
                else {
                    if (age == -1) {
                        double cost = result * (1 - reduction / 100.0);
                        res.set_content("{ \"cost\": " + std::to_string((int)std::ceil(cost)) + "}", "application/json");
                    }
                    else {
                        if (age > 64) {
                            double cost = result * .75 * (1 - reduction / 100.0);
                            res.set_content("{ \"cost\": " + std::to_string((int)std::ceil(cost)) + "}", "application/json");
                        }
                        else {
                            double cost = result * (1 - reduction / 100.0);
                            res.set_content("{ \"cost\": " + std::to_string((int)std::ceil(cost)) + "}", "application/json");
                        }
                    }
                }
            }
            else {
                if (age != -1 && age >= 6) {
                    if (age > 64) {
                        res.set_content("{ \"cost\": " + std::to_string((int)std::ceil(result * .4)) + "}", "application/json");
                    }
                    else {
                        res.set_content("{ \"cost\": " + std::to_string(result) + "}", "application/json");
                    }
                }
                else {
                    res.set_content("{ \"cost\": 0}", "application/json");
                }
            }
        }
    });
}
