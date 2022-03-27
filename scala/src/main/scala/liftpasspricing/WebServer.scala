package liftpasspricing

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}

object WebServer {

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 5010).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(
          """LiftPassPricing Api started on {},
            |you can open http://{}:{}/prices?type=night&age=23&date=2019-02-18 in a navigator
            |and you'll get the price of the list pass for the day.""".stripMargin,
          address.getPort,
          address.getHostString,
          address.getPort
        )
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val (routes, _) = new LiftPassPricing().createApp()
      startHttpServer(routes)(context.system)

      Behaviors.empty
    }

    ActorSystem[Nothing](rootBehavior, "LiftPassPricing")
    ()
  }

}
