name := "youi-plugin"
organization := "io.youi"
version := "1.0.0"
sbtPlugin := true
scalaVersion := "2.10.6"
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.18")
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value