name := """MagicalMusic"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.webjars" % "bootstrap" % "2.3.1",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "com.typesafe.play" %% "play-slick" % "1.0.1", 
  "com.google.apis" % "google-api-services-youtube" % "v3-rev148-1.20.0",
  "com.google.gdata" % "core" % "1.47.1"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true