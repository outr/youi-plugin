package io.youi.plugin

import sbt._
import org.scalajs.sbtplugin.cross.CrossProject

import scala.language.experimental.macros
import scala.language.implicitConversions

object YouIPlugin extends AutoPlugin {
  // TODO: add youi dependencies?
  // TODO: restart?

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