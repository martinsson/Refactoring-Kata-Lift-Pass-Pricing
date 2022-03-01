import Dependencies._

ThisBuild / scalaVersion := "2.13.1"

lazy val root = (project in file("."))
  .settings(
    name := "lift-pass-pricing",
    description := "Lift-Pass-Pricing refactoring kata",
    libraryDependencies += `akka-actor`,
    libraryDependencies += `akka-http`,
    libraryDependencies += `akka-http-testkit`,
    libraryDependencies += `akka-http-spray-json`,
    libraryDependencies += `akka-stream`,
    libraryDependencies += `akka-stream-testkit`,
    libraryDependencies += `mysql-connector-java`,
    libraryDependencies += scalaTest % Test
  )
