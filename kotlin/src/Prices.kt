package dojo

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.sql.Connection
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*


object Prices {
    fun createApp(): Pair<Connection, ApplicationEngine> {
        val connection: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql")
        val app = embeddedServer(Netty, 4567) {
            routing {
                put("/prices") {
                    val liftPassCost: Int = call.request.queryParameters["cost"]!!.toInt()
                    val liftPassType: String? = call.request.queryParameters["type"]

                    connection.prepareStatement(
                            """
                            INSERT INTO base_price (type, cost) VALUES (?, ?)
                            ON DUPLICATE KEY UPDATE cost = ?
                            """.trimIndent()
                    ).use { stmt ->
                        with(stmt) {
                            setString(1, liftPassType)
                            setInt(2, liftPassCost)
                            setInt(3, liftPassCost)
                            execute()
                        }
                    }
                    call.respond("")
                }

                get("/prices") {
                    val age = call.request.queryParameters["age"]?.toInt()

                    val response = connection.prepareStatement(
                            """
                                SELECT cost FROM base_price
                                WHERE type = ?
                            """.trimIndent()
                    ).use { costStmt ->

                        costStmt.setString(1, call.request.queryParameters["type"])

                        costStmt.use {
                            val result = it.executeQuery()
                            result.next()

                            var reduction = 0
                            var isHoliday = false

                            if (age != null && age < 6) {
                                """
                                { "cost": 0 }
                            """.trimIndent()
                            } else {
                                reduction = 0
                                if (call.request.queryParameters["type"] != "night") {
                                    val isoFormat = SimpleDateFormat("yyyy-MM-dd")
                                    connection.prepareStatement(
                                            "SELECT * FROM holidays"
                                    ).use { holidayStmt ->

                                        val holidays = holidayStmt.executeQuery()
                                        while (holidays.next()) {
                                            val holiday = holidays.getDate("holiday")
                                            if (call.request.queryParameters["date"] != null) {
                                                val d = isoFormat.parse(call.request.queryParameters["date"])
                                                if (d.year == holiday.year
                                                        && d.month == holiday.month
                                                        && d.date == holiday.date
                                                ) {
                                                    isHoliday = true
                                                }
                                            }
                                        }
                                    }


                                    if (call.request.queryParameters["date"] != null) {
                                        val calendar = Calendar.getInstance()
                                        calendar.time = isoFormat.parse(call.request.queryParameters["date"])
                                        if (!isHoliday && calendar[Calendar.DAY_OF_WEEK] == 2) {
                                            reduction = 35
                                        }
                                    }

                                    // TODO apply reduction for others
                                    if (age != null && age < 15) {
                                        """
                                           { "cost": ${Math.ceil(result.getInt("cost") * .7).toInt()} } 
                                        """.trimIndent()
                                    } else {
                                        if (age == null) {
                                            val cost = result.getInt("cost") * (1 - reduction / 100.0)
                                            """
                                            {"cost": ${Math.ceil(cost).toInt()} } 
                                            """.trimIndent()
                                        } else {
                                            if (age > 64) {
                                                val cost = result.getInt("cost") * .75 * (1 - reduction / 100.0)
                                                """
                                                {"cost": ${Math.ceil(cost).toInt()} } 
                                                """.trimIndent()
                                            } else {
                                                val cost = result.getInt("cost") * (1 - reduction / 100.0)
                                                """
                                                {"cost": ${Math.ceil(cost).toInt()} } 
                                                """.trimIndent()
                                            }
                                        }
                                    }
                                } else {
                                    if (age != null && age >= 6) {
                                        if (age > 64) {
                                            """
                                           { "cost": ${Math.ceil(result.getInt("cost") * .4).toInt()} } 
                                        """.trimIndent()
                                        } else {
                                            """
                                            { "cost": ${result.getInt("cost")}  }    
                                        """.trimIndent()
                                        }
                                    } else {
                                        """
                                    { "cost": 0 }    
                                    """.trimIndent()
                                    }
                                }
                            }

                        }
                    }
                    call.respondText(response, ContentType.Application.Json)
                }
            }
        }
        return connection to app
    }
}
