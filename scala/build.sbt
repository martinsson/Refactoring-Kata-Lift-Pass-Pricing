ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "lift-pass-pricing",
    description := "Lift-Pass-Pricing refactoring kata",

    libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19",
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.9",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.9",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.19",

    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11" % Runtime,
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28" % Runtime,

    libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.19" % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.2.9" % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.19" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test
  )
