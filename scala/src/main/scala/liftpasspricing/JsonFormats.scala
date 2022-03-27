package liftpasspricing

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

object JsonFormats {

  implicit val costFormat: RootJsonFormat[Cost] = jsonFormat1(Cost)

}
