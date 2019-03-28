import sbt._

object Dependencies {
  private lazy val akkaHttpVersion: String = "10.1.8"
  private lazy val akkaVersion: String = "2.5.21"
  lazy val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val `akka-http` = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test
  lazy val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  lazy val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
  lazy val `mysql-connector-java` = "mysql" % "mysql-connector-java" % "8.0.15" % Runtime
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.6"
}
