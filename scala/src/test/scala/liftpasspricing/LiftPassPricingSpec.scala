package liftpasspricing

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

import scala.util.Try

class LiftPassPricingSpec extends FlatSpec with ScalatestRouteTest with JsonSupport {

  def withLiftPassPricing(testCode: Route => Any): Unit = {
    val liftPassPrincing = new LiftPassPricing()
    try {
      Put("/prices?type=1jour&cost=35") ~> liftPassPrincing.routes ~> check(status shouldBe OK)
      Put("/prices?type=night&cost=19") ~> liftPassPrincing.routes ~> check(status shouldBe OK)
      testCode(liftPassPrincing.routes)
    } finally {
      Try(liftPassPrincing.connection.close())
    }
  }

  "Lift pass pricing api" should "default cost" in withLiftPassPricing { app =>
    Get("/prices?type=1jour") ~> app ~> check {

      responseAs[Cost] shouldBe Cost(35)
    }
  }

  it should "works for all ages" in withLiftPassPricing { app =>
    List(
      (5, 0),
      (6, 25),
      (14, 25),
      (15, 35),
      (25, 35),
      (64, 35),
      (65, 27),
    ).foreach { case (age, expectedCost) =>
      Get(s"/prices?type=1jour&age=$age") ~> app ~> check {

        responseAs[Cost] shouldBe Cost(expectedCost)
      }
    }
  }

  ignore should "default night cost" in withLiftPassPricing { app =>
    Get(s"/prices?type=night") ~> app ~> check {

      responseAs[Cost] shouldBe Cost(19)
    }
  }

  it should "works for night passes" in withLiftPassPricing { app =>
    List(
      (5, 0),
      (6, 19),
      (25, 19),
      (64, 19),
      (65, 8),
    ).foreach { case (age, expectedCost) =>
      Get(s"/prices?type=night&age=$age") ~> app ~> check {

        responseAs[Cost] shouldBe Cost(expectedCost)
      }
    }
  }

  it should "works for monday deals" in withLiftPassPricing { app =>
    List(
      (15, 35, "2019-02-22"),
      (15, 35, "2019-02-25"),
      (15, 23, "2019-03-11"),
      (65, 18, "2019-03-11"),
    ).foreach { case (age, expectedCost, date) =>
      Get(s"/prices?type=1jour&age=$age&date=$date") ~> app ~> check {

        responseAs[Cost] shouldBe Cost(expectedCost)
      }
    }
  }

  // TODO 2-4, and 5, 6 day pass

}
