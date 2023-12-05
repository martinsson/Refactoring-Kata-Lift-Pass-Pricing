package liftpasspricing

import liftpasspricing.JsonFormats._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.StatusCodes.OK
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

import scala.Function.const
import scala.util.Using

class LiftPassPricingSpec extends AnyFunSpec with ScalatestRouteTest {

  describe("prices") {

    def withPrices(testCode: Route => Any): Unit = {
      val (routes, connection) = new LiftPassPricing().createApp()
      Using(connection)(const {
        Put("/prices?type=1jour&cost=35") ~> routes ~> check(status shouldBe OK)
        Put("/prices?type=night&cost=19") ~> routes ~> check(status shouldBe OK)
        testCode(routes)
      })
    }

    it("default cost") {
      withPrices { app =>
        Get("/prices?type=1jour") ~> app ~> check {

          responseAs[Cost] shouldBe Cost(35)
        }
      }
    }

    it("works for all ages") {
      withPrices { app =>
        List(
          (5, 0),
          (6, 25),
          (14, 25),
          (15, 35),
          (25, 35),
          (64, 35),
          (65, 27)
        ).foreach { case (age, expectedCost) =>
          Get(s"/prices?type=1jour&age=$age") ~> app ~> check {

            responseAs[Cost] shouldBe Cost(expectedCost)
          }
        }
      }
    }

    ignore("default night cost") {
      withPrices { app =>
        Get(s"/prices?type=night") ~> app ~> check {

          responseAs[Cost] shouldBe Cost(19)
        }
      }
    }

    it("works for night passes") {
      withPrices { app =>
        List(
          (5, 0),
          (6, 19),
          (25, 19),
          (64, 19),
          (65, 8)
        ).foreach { case (age, expectedCost) =>
          Get(s"/prices?type=night&age=$age") ~> app ~> check {

            responseAs[Cost] shouldBe Cost(expectedCost)
          }
        }
      }
    }

    it("works for monday deals") {
      withPrices { app =>
        List(
          (15, 35, "2019-02-22"),
          (15, 35, "2019-02-25"),
          (15, 23, "2019-03-11"),
          (65, 18, "2019-03-11")
        ).foreach { case (age, expectedCost, date) =>
          Get(s"/prices?type=1jour&age=$age&date=$date") ~> app ~> check {

            responseAs[Cost] shouldBe Cost(expectedCost)
          }
        }
      }
    }

    // TODO 2-4, and 5, 6 day pass

  }
}
