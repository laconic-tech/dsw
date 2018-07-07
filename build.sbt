name := "dsw"
version := "0.1"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-Ypartial-unification")


val Http4sVersion = "0.18.13"

libraryDependencies ++= Seq(
  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,

  // helm
  "io.netty" % "netty-tcnative-boringssl-static" % "2.0.6.Final",
  "org.microbean" % "microbean-helm" % "2.8.2.1.1.0" exclude("io.netty", "netty-tcnative-boringssl-static"),

  // converterss
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",

  // testing
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)