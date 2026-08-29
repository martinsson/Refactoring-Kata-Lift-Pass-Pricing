package dojo

import io.ktor.application.*
import io.ktor.features.*
import org.slf4j.LoggerFactory
import java.sql.SQLException

fun main(args: Array<String>) {
    val (connection, app) = Prices.createApp()
    app.start()

    println(
            """
            LiftPassPricing Api started on 4567,
            you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator
            and you'll get the price of the list pass for the day.
            """.trimIndent()
    )

    app.environment.monitor.subscribe(ApplicationStopping) {
        try {
            connection.close()
        } catch (e: SQLException) {
            LoggerFactory.getLogger("Main").error("connection close", e)
        }
    }

}
