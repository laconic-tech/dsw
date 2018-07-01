name := "dsw"
version := "0.1"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-Ypartial-unification")


val Http4sVersion = "0.18.13"

libraryDependencies ++= Seq(
  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
)