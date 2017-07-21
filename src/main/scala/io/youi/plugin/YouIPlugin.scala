package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject

import scala.language.implicitConversions

object YouIPlugin extends AutoPlugin {
  def crossApplication(name: String): PartialCrossApplication = new PartialCrossApplication(name)

  implicit def crossApplicationDependency(ca: CrossApplication): CrossApplicationDependencies = {
    CrossApplicationDependencies(ClasspathDependency(ca.js, None), ClasspathDependency(ca.jvm, None))
  }

  implicit def crossProjectDependency(cp: CrossProject): CrossApplicationDependencies = {
    CrossApplicationDependencies(ClasspathDependency(cp.js, None), ClasspathDependency(cp.jvm, None))
  }
}

class PartialCrossApplication(name: String) {
  def in(dir: File): CrossApplication = CrossApplication(
    js = Project(s"${name}JS", new File(dir, "js")),
    jvm = Project(s"${name}JVM", new File(dir, "jvm"))
  ).jsSettings(
    artifactPath in Compile in fastOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-fastopt.js"),
    artifactPath in Test in fastOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-fastopt.js"),
    artifactPath in fullOptJS := (baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app" / "application-opt.js"),
    crossTarget in packageJSDependencies := baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app",
    crossTarget in packageMinifiedJSDependencies := baseDirectory.value / ".." / "jvm" / "src" / "main" / "resources" / "app"
  ).enableJSPlugins(ScalaJSPlugin)
}

case class CrossApplication(js: Project, jvm: Project) {
  def dependsOn(deps: CrossApplicationDependencies*): CrossApplication = {
    copy(js = js.dependsOn(deps.map(_.js): _*), jvm = jvm.dependsOn(deps.map(_.jvm): _*))
  }

  def enablePlugins(plugins: Plugins*): CrossApplication = {
    copy(js = js.enablePlugins(plugins: _*), jvm = jvm.enablePlugins(plugins: _*))
  }

  def enableJSPlugins(plugins: Plugins*): CrossApplication = {
    copy(js = js.enablePlugins(plugins: _*))
  }

  def enableJVMPlugins(plugins: Plugins*): CrossApplication = {
    copy(jvm = jvm.enablePlugins(plugins: _*))
  }

  def disablePlugins(plugins: AutoPlugin*): CrossApplication = {
    copy(js = js.disablePlugins(plugins: _*), jvm = jvm.disablePlugins(plugins: _*))
  }

  def disableJSPlugins(plugins: AutoPlugin*): CrossApplication = {
    copy(js = js.disablePlugins(plugins: _*))
  }

  def disableJVMPlugins(plugins: AutoPlugin*): CrossApplication = {
    copy(jvm = jvm.disablePlugins(plugins: _*))
  }

  def settings(settings: Def.SettingsDefinition*): CrossApplication = {
    copy(js = js.settings(settings: _*), jvm = jvm.settings(settings: _*))
  }

  def jsSettings(settings: Def.SettingsDefinition*): CrossApplication = {
    copy(js = js.settings(settings: _*))
  }

  def jvmSettings(settings: Def.SettingsDefinition*): CrossApplication = {
    copy(jvm = jvm.settings(settings: _*))
  }
}

case class CrossApplicationDependencies(js: ClasspathDependency, jvm: ClasspathDependency)