ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "lift-pass-pricing",
    description := "Lift-Pass-Pricing refactoring kata",

    libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % "1.0.1",
    libraryDependencies += "org.apache.pekko" %% "pekko-http" % "1.0.0",
    libraryDependencies += "org.apache.pekko" %% "pekko-http-spray-json" % "1.0.0",
    libraryDependencies += "org.apache.pekko" %% "pekko-stream" % "1.0.1",

    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.7" % Runtime,
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33" % Runtime,

    libraryDependencies += "org.apache.pekko" %% "pekko-actor-testkit-typed" % "1.0.1" % Test,
    libraryDependencies += "org.apache.pekko" %% "pekko-http-testkit" % "1.0.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.16" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.16" % Test
  )
