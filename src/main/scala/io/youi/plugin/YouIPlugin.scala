package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object YouIPlugin extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Seq(
    artifactPath in Compile in fastOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-fastopt.js"),
    artifactPath in Test in fastOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-fastopt.js"),
    artifactPath in fullOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-opt.js"),
    crossTarget in packageJSDependencies := baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app",
    crossTarget in packageMinifiedJSDependencies := baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app"
  )
}