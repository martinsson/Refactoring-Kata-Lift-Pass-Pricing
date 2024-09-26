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

    Runtime.getRuntime().addShutdownHook(Thread() {
        try {
            println("closing connexion")
            connection.close()
        } catch (e: SQLException) {
            LoggerFactory.getLogger("Main").error("connection close", e)
        }
    })
}

fun main2(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
    }

}

