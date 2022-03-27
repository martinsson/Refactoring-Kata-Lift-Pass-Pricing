package liftpasspricing

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import liftpasspricing.JsonFormats._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.Function.const
import scala.util.Using

class LiftPassPricingSpec extends AnyFlatSpec with ScalatestRouteTest {

  def withLiftPassPricing(testCode: Route => Any): Unit = {
    val (routes, connection) = new LiftPassPricing().createApp()
    Using(connection)(const(testCode(routes)))
  }

  it should "does something" in withLiftPassPricing { app =>

    Get("/prices?type=1jour") ~> app ~> check {

      val exptectedResult = Cost(35) // change this to make the test pass
      responseAs[Cost] shouldBe exptectedResult
    }
  }

}
