package liftpasspricing

import java.sql.{Connection, DriverManager}
import java.time.LocalDate

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.{HttpApp, Route}

class LiftPassPricing extends HttpApp with JsonSupport {

  val undefined: String = Int.MaxValue.toString

  val (host, user, database, password) = ("localhost", "root", "lift_pass", "mysql")
  val connection: Connection = DriverManager.getConnection(s"jdbc:mysql://$host/$database", user, password)

  override def routes: Route =
    path("prices") {
      (put & parameters('cost, 'type)) { (liftPassCost, liftPassType) =>
        val statement = connection.prepareStatement(
          "INSERT INTO `base_price` (type, cost) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE cost = ?")
        statement.setString(1, liftPassType)
        statement.setString(2, liftPassCost)
        statement.setString(3, liftPassCost)
        statement.executeUpdate()

        complete(OK)
      } ~ (get & parameterMap) { parameters =>
        val req = parameters.withDefault {
          case "age" => undefined
          case "date" => "1981-12-24"
          case _ => ""
        }
        val costStatement = connection.prepareStatement(
          "SELECT cost FROM `base_price` " +
            "WHERE `type` = ?")
        costStatement.setString(1, req("type"))
        val result = costStatement.executeQuery()
        result.next()

        var reduction: Int = 0
        var isHoliday: Boolean = false
        if (req("age").toInt < 6) {
          complete(Cost(0))
        } else {
          reduction = 0
          if (req("type") != "night") {
            val holidays = connection.createStatement().executeQuery(
              "SELECT * FROM `holidays`"
            )

            while (holidays.next) {
              val holiday: LocalDate = holidays.getDate(1).toLocalDate
              if (req.contains("date")) {
                val d = LocalDate.parse(req("date"))
                if (d.getYear == holiday.getYear
                  && d.getMonth == holiday.getMonth
                  && d.getDayOfMonth == holiday.getDayOfMonth) {

                  isHoliday = true
                }
              }
            }

            if (!isHoliday && LocalDate.parse(req("date")).getDayOfWeek.getValue == 1) {
              reduction = 35
            }

            // TODO apply reduction for others
            if (req("age").toInt < 15) {
              complete(Cost(math.ceil(result.getInt("cost") * .7).toInt))
            } else {
              if (req("age") == undefined) {
                val cost = result.getInt("cost") * (1 - reduction / 100d)
                complete(Cost(math.ceil(cost).toInt))
              } else {
                if (req("age").toInt > 64) {
                  val cost = result.getInt("cost") * .75 * (1 - reduction / 100d)
                  complete(Cost(math.ceil(cost).toInt))
                } else {
                  val cost = result.getInt("cost") * (1 - reduction / 100d)
                  complete(Cost(math.ceil(cost).toInt))
                }
              }
            }
          } else {
            if (req("age").toInt >= 6) {
              if (req("age").toInt > 64) {
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

}
