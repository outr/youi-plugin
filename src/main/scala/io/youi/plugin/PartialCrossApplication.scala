package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

class PartialCrossApplication(id: String) {
  def in(dir: File): CrossApplication = {
    val baseDir = dir.getAbsoluteFile
    val sharedSource = baseDir / "shared" / "src"
    val jvmSource = baseDir / "jvm" / "src"
    val jvmResources = jvmSource / "main" / "resources"
    val jvmApp = jvmResources / "app"
    CrossApplication(
      js = Project(s"${id}JS", new File(dir, "js")),
      jvm = Project(s"${id}JVM", new File(dir, "jvm"))
    ).settings(
      // Add shared source
      unmanagedSourceDirectories in Compile ++= {
        makeCrossSources(Some(sharedSource / "main" / "scala"), scalaBinaryVersion.value, crossPaths.value)
      },
      unmanagedSourceDirectories in Test ++= {
        makeCrossSources(Some(sharedSource / "test" / "scala"), scalaBinaryVersion.value, crossPaths.value)
      }
    ).jsSettings(
      artifactPath in Compile in fastOptJS := jvmApp / "application-fastopt.js",
      artifactPath in Test in fastOptJS := jvmApp / "application-fastopt.js",
      artifactPath in Compile in fullOptJS := jvmApp / "application.js",
      artifactPath in Test in fullOptJS := jvmApp / "application.js",
      artifactPath in Compile in packageJSDependencies := jvmApp / "application-jsdeps.js",
      artifactPath in Test in packageJSDependencies := jvmApp / "application-jsdeps.js",
      artifactPath in Compile in packageMinifiedJSDependencies := jvmApp / "application-jsdeps.min.js",
      artifactPath in Test in packageMinifiedJSDependencies := jvmApp / "application-jsdeps.min.js",
      skip in packageJSDependencies := false
    ).enableJSPlugins(ScalaJSPlugin)
  }

  // Inspired by sbt's Defaults.makeCrossSources
  private def makeCrossSources(sharedSrcDir: Option[File],
                               scalaBinaryVersion: String, cross: Boolean): Seq[File] = {
    sharedSrcDir.fold[Seq[File]] {
      Seq.empty
    } { srcDir =>
      if (cross)
        Seq(srcDir.getParentFile / s"${srcDir.name}-$scalaBinaryVersion", srcDir)
      else
        Seq(srcDir)
    }
  }
}