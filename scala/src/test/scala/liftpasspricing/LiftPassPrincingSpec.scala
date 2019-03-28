package liftpasspricing

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}

class LiftPassPrincingSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  "Lift pass pricing api" should "get price" in {
    Get("/prices?type=1jour&age=22") ~> LiftPassPricing.routes ~> check {
      handled shouldBe true
    }
  }

}
