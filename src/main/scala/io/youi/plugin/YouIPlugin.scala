package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject

import scala.language.experimental.macros
import scala.reflect.macros.Context

import scala.language.implicitConversions

object YouIPlugin extends AutoPlugin {
  object autoImport {
    def crossApplication: PartialCrossApplication = macro CrossApplication.partial

    implicit def crossApplicationDependency(ca: CrossApplication): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(ca.js, None), ClasspathDependency(ca.jvm, None))
    }

    implicit def crossProjectDependency(cp: CrossProject): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(cp.js, None), ClasspathDependency(cp.jvm, None))
    }
  }
}

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

object CrossApplication {
  def partial(c: Context): c.Expr[PartialCrossApplication] = {
    import c.universe._

    val enclosingValName = definingValName(c, methodName =>
      s"""$methodName must be directly assigned to a val, such as `val x = $methodName`.""")
    val name = c.Expr[String](Literal(Constant(enclosingValName)))
    reify(new PartialCrossApplication(name.splice))
  }

  private def definingValName(c: Context, invalidEnclosingTree: String => String): String = {
    import c.universe._
    val methodName = c.macroApplication.symbol.name

    // trim is not strictly correct, but macros don't expose the API necessary
    def processName(n: Name): String = n.decoded.trim

    def enclosingVal(trees: List[c.Tree]): String = trees match {
      case vd @ ValDef(_, name, _, _) :: ts =>
        processName(name)

      case (_: Apply | _: Select | _: TypeApply) :: xs =>
        enclosingVal(xs)

      // lazy val x: X = <methodName> has this form for some reason
      // (only when the explicit type is present, though)
      case Block(_, _) :: DefDef(mods, name, _, _, _, _) :: xs if mods.hasFlag(Flag.LAZY) =>
        processName(name)
      case _ =>
        c.error(c.enclosingPosition, invalidEnclosingTree(methodName.decoded))
        "<error>"
    }

    enclosingVal(enclosingTrees(c).toList)
  }

  def enclosingTrees(c: Context): Seq[c.Tree] =
    c.asInstanceOf[reflect.macros.runtime.Context].callsiteTyper.
      context.enclosingContextChain.map(_.tree.asInstanceOf[c.Tree])
}

case class CrossApplicationDependencies(js: ClasspathDependency, jvm: ClasspathDependency)