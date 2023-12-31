ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0"
libraryDependencies += "com.bot4s" %% "telegram-akka" % "5.6.3"

libraryDependencies += "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.8.13"

lazy val root = (project in file("."))
  .settings(
    name := "sample_server"
  )
