package liftpasspricing

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val costFormat: RootJsonFormat[Cost] = jsonFormat1(Cost)

}
