lazy val commonSettings = Seq(
  organization := "com.github.domesc",
  version := "0.1",
  scalaVersion := "2.12.6",
  test in assembly := {}
)

lazy val commonLibs = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-simple" % "1.6.4"
)


lazy val kafkaLibs = Seq(
  "net.cakesolutions" %% "scala-kafka-client" % "1.1.0",
  "com.typesafe" % "config" % "1.3.2"
)

lazy val kafkaStreamsLibs = Seq(
  "com.lightbend" %% "kafka-streams-scala" % "0.2.1"
)

lazy val kafka = (project in file("kafka"))
  .settings(commonSettings: _*)
  .settings(assemblyJarName in assembly := "kafka.jar")
  .settings(libraryDependencies ++= kafkaLibs)

lazy val kafkaStreams = (project in file("kafkastreams"))
  .settings(commonSettings: _*)
  .settings(assemblyJarName in assembly := "kafkastreams.jar")
  .settings(libraryDependencies ++= commonLibs ++ kafkaStreamsLibs)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(name := "StreamingFun")
  .aggregate(kafka, kafkaStreams)


resolvers += Resolver.bintrayRepo("cakesolutions", "maven")
