name := "youi-plugin"
organization := "io.youi"
version := "1.0.1"
sbtPlugin := true
crossSbtVersions := Vector("0.13.16", "1.0.4")
// Workaround for https://github.com/sbt/sbt/issues/3393
// This will be solved in sbt 0.13.17
libraryDependencies += {
  val currentSbtVersion = (sbtBinaryVersion in pluginCrossBuild).value
  Defaults.sbtPluginExtra("org.scala-js" % "sbt-scalajs" % "0.6.21", currentSbtVersion, (scalaBinaryVersion in update).value)
}
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value