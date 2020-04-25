package liftpasspricing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.util.Try

class LiftPassPricingSpec extends AnyFlatSpec with ScalatestRouteTest with JsonSupport {

  def withLiftPassPricing(testCode: Route => Any): Unit = {
    val liftPassPrincing = new LiftPassPricing()
    try {
      testCode(liftPassPrincing.routes)
    } finally {
      Try(liftPassPrincing.connection.close())
    }
  }

  it should "does something" in withLiftPassPricing { app =>

    Get("/prices?type=1jour") ~> app ~> check {

      val exptectedResult = Cost(35) // change this to make the test pass
      responseAs[Cost] shouldBe exptectedResult
    }
  }

}
