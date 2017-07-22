package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.cross.CrossProject

import scala.language.experimental.macros
import scala.language.implicitConversions

object YouIPlugin extends AutoPlugin {
  object autoImport {
    val youiVersion: SettingKey[String] = settingKey[String]("YouI version to use. Required.")
    val youiServer: SettingKey[String] = settingKey[String]("YouI server implementation to use. Defaults to 'undertow'.")
    val youiInclude: SettingKey[Boolean] = settingKey[Boolean]("Whether YouI dependencies should be automatically included. Defaults to true.")

    def crossApplication: PartialCrossApplication = macro CrossApplication.partial

    implicit def crossApplicationDependency(ca: CrossApplication): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(ca.js, None), ClasspathDependency(ca.jvm, None))
    }

    implicit def crossProjectDependency(cp: CrossProject): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(cp.js, None), ClasspathDependency(cp.jvm, None))
    }
  }
}