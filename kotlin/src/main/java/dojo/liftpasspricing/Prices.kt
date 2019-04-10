package dojo.liftpasspricing

import spark.kotlin.after
import spark.kotlin.get
import spark.kotlin.port
import spark.kotlin.put
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

object Prices {

    const val port: Int = 4567

    @Throws(SQLException::class)
    fun createApp(): Connection {

        val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql")

        port(port)

        put("/prices") {
            val req = this.request
            val liftPassCost = Integer.parseInt(req.queryParams("cost"))
            val liftPassType = req.queryParams("type")

            connection.prepareStatement( //
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?"
            ).use { stmt ->
                stmt.setString(1, liftPassType)
                stmt.setInt(2, liftPassCost)
                stmt.setInt(3, liftPassCost)
                stmt.execute()
            }

            ""
        }

        get("/prices") {
            val req = this.request
            val age = if (req.queryParams("age") != null) Integer.valueOf(req.queryParams("age")) else null

            connection.prepareStatement( //
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?"
            ).use { costStmt ->
                costStmt.setString(1, req.queryParams("type"))
                costStmt.executeQuery().use { result ->
                    result.next()

                    var reduction: Int
                    var isHoliday = false

                    if (age != null && age < 6) {
                        return@get "{ \"cost\": 0}"
                    } else {
                        reduction = 0

                        if (!req.queryParams("type").equals("night")) {
                            val isoFormat = SimpleDateFormat("yyyy-MM-dd")

                            connection.prepareStatement( //
                                "SELECT * FROM holidays"
                            ).use { holidayStmt ->
                                holidayStmt.executeQuery().use { holidays ->

                                    while (holidays.next()) {
                                        val holiday = holidays.getDate("holiday")
                                        if (req.queryParams("date") != null) {
                                            val d = isoFormat.parse(req.queryParams("date"))
                                            if (d.year == holiday.year && //

                                                d.month == holiday.month && //

                                                d.date == holiday.date
                                            ) {
                                                isHoliday = true
                                            }
                                        }
                                    }

                                }
                            }

                            if (req.queryParams("date") != null) {
                                val calendar = Calendar.getInstance()
                                calendar.time = isoFormat.parse(req.queryParams("date"))
                                if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                                    reduction = 35
                                }
                            }

                            // TODO apply reduction for others
                            if (age != null && age < 15) {
                                return@get "{ \"cost\": " + Math.ceil(result.getInt("cost") * .7).toInt() + "}"
                            } else {
                                if (age == null) {
                                    val cost = result.getInt("cost") * (1 - reduction / 100.0)
                                    return@get "{ \"cost\": " + Math.ceil(cost).toInt() + "}"
                                } else {
                                    if (age > 64) {
                                        val cost = result.getInt("cost").toDouble() * .75 * (1 - reduction / 100.0)
                                        return@get "{ \"cost\": " + Math.ceil(cost).toInt() + "}"
                                    } else {
                                        val cost = result.getInt("cost") * (1 - reduction / 100.0)
                                        return@get "{ \"cost\": " + Math.ceil(cost).toInt() + "}"
                                    }
                                }
                            }
                        } else {
                            if (age != null && age >= 6) {
                                if (age > 64) {
                                    return@get "{ \"cost\": " + Math.ceil(result.getInt("cost") * .4).toInt() + "}"
                                } else {
                                    return@get "{ \"cost\": " + result.getInt("cost") + "}"
                                }
                            } else {
                                return@get "{ \"cost\": 0}"
                            }
                        }
                    }
                }
            }
        }

        after { this.response.type("application/json") }

        return connection
    }

}
