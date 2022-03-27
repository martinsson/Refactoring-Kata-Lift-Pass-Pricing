package liftpasspricing

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import liftpasspricing.JsonFormats._

import java.sql.{Connection, DriverManager}
import java.time.LocalDate
import scala.Array.emptyByteArray
import scala.util.Try

class LiftPassPricing {

  def createApp(): (Route, Connection) = {
    val (host, user, database, password) = ("localhost", "root", "lift_pass", "mysql")
    val connection: Connection = DriverManager.getConnection(s"jdbc:mysql://$host/$database", user, password)

    val routes = path("prices") {
      (put & parameters(Symbol("cost"), Symbol("type"))) { (liftPassCost, liftPassType) =>
        val statement = connection.prepareStatement(
          "INSERT INTO `base_price` (type, cost) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE cost = ?"
        )
        statement.setString(1, liftPassType)
        statement.setString(2, liftPassCost)
        statement.setString(3, liftPassCost)
        statement.executeUpdate()

        complete(HttpEntity(`application/json`, emptyByteArray))
      } ~ (get & parameterMap) { req =>
        val costStatement = connection.prepareStatement(
          "SELECT cost FROM `base_price` " +
            "WHERE `type` = ?"
        )
        costStatement.setString(1, req("type"))
        val result = costStatement.executeQuery()
        result.next()

        if (Try(req("age").toInt < 6).getOrElse(false)) {
          complete(Cost(0))
        } else {
          if (req("type") != "night") {
            val holidays = connection
              .createStatement()
              .executeQuery(
                "SELECT * FROM `holidays`"
              )

            var isHoliday: Boolean = false
            var reduction: Int = 0
            while (holidays.next) {
              val holiday: LocalDate = holidays.getDate(1).toLocalDate
              if (req.contains("date")) {
                val d = LocalDate.parse(req("date"))
                if (
                  d.getYear == holiday.getYear
                  && d.getMonth == holiday.getMonth
                  && d.getDayOfMonth == holiday.getDayOfMonth
                ) {

                  isHoliday = true
                }
              }
            }

            if (!isHoliday && Try(LocalDate.parse(req("date")).getDayOfWeek.getValue == 1).getOrElse(false)) {
              reduction = 35
            }

            // TODO apply reduction for others
            if (Try(req("age").toInt < 15).getOrElse(false)) {
              complete(Cost(math.ceil(result.getInt("cost") * .7).toInt))
            } else {
              if (!req.contains("age")) {
                val cost = result.getInt("cost") * (1 - reduction / 100d)
                complete(Cost(math.ceil(cost).toInt))
              } else {
                if (Try(req("age").toInt > 64).getOrElse(false)) {
                  val cost = result.getInt("cost") * .75 * (1 - reduction / 100d)
                  complete(Cost(math.ceil(cost).toInt))
                } else {
                  val cost = result.getInt("cost") * (1 - reduction / 100d)
                  complete(Cost(math.ceil(cost).toInt))
                }
              }
            }
          } else {
            if (Try(req("age").toInt >= 6).getOrElse(false)) {
              if (Try(req("age").toInt > 64).getOrElse(false)) {
                complete(Cost(math.ceil(result.getInt("cost") * .4).toInt))
              } else {
                complete(Cost(result.getInt("cost")))
              }
            } else {
              complete(Cost(0))
            }
          }
        }
      }
    }

    (routes, connection)
  }

}
