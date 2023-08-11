package liftpasspricing

import liftpasspricing.JsonFormats._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
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
      Using(connection)(const(testCode(routes)))
    }

    it("does something") {
      withPrices { app =>
        Get("/prices?type=1jour") ~> app ~> check {

          val expectedResult = Cost(35) // change this to make the test pass
          responseAs[Cost] shouldBe expectedResult
        }
      }
    }
  }

}
