organization := "lt.dvim.vent"
name := "master-server-stream"
description := "A stream of server addresses from the Valve's Master Server"

scalaVersion := "2.12.6"
libraryDependencies ++= Seq(
  "lt.dvim.msqp"       %% "msqp4s"                  % "0.1",
  "com.typesafe.akka"  %% "akka-stream"             % "2.5.13",
  "com.lightbend.akka" %% "akka-stream-alpakka-udp" % "0.19+33-b4c042ab",
  "com.lihaoyi"        %% "utest"                   % "0.6.3" % "test"
)

resolvers += Resolver.bintrayRepo("2m", "maven")

testFrameworks += new TestFramework("utest.runner.Framework")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/2m/master-server-stream"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/2m/master-server-stream"),
    "git@github.com:2m/master-server-stream.git"
  )
)
developers += Developer(
  "contributors",
  "Contributors",
  "https://gitter.im/2m/master-server-stream",
  url("https://github.com/2m/master-server-stream/graphs/contributors")
)
organizationName := "https://github.com/2m/master-server-stream/graphs/contributors"
startYear := Some(2018)

bintrayOrganization := Some("2m")
bintrayRepository := (if (isSnapshot.value) "snapshots"
                      else bintrayRepository.value)

scalafmtOnCompile := true
enablePlugins(AutomateHeaderPlugin)
