package io.youi.plugin

import sbt._
import Keys._
import org.scalajs.sbtplugin.cross.CrossProject

import scala.language.experimental.macros
import scala.language.implicitConversions

object YouIPlugin extends AutoPlugin {
  object autoImport {
    val youiVersion: SettingKey[String] = settingKey[String]("Version of youi to use. Required.")
    val youiServer: SettingKey[String] = settingKey[String]("Server implementation for youi to use. Defaults to 'undertow'.")
    val youiInclude: SettingKey[Boolean] = settingKey[Boolean]("Whether youi dependencies should be automatically included. Defaults to true.")
    val youiAddExtension: SettingKey[String] = settingKey[String]("Defines the extension to add to copied files. Set to empty String to leaver original. Defaults to '.youi'.")
    val youiPath: SettingKey[File] = settingKey[File]("Defines the directory Scala.js files are output to. Defaults to the JVM project's `src/main/resources/app` directory.")
    val youiFastOpt: SettingKey[String] = settingKey[String]("Defines the extra part of the file name for fastOptJS. Set to blank String to exclude. Defaults to '-fastopt'.")
    val youiFullOpt: SettingKey[String] = settingKey[String]("Defines the extra part of the file name for fullOptJS. Set to blank String to exclude. Defaults to blank.")

    def crossApplication: PartialCrossApplication = macro CrossApplication.partial

    implicit def crossApplicationDependency(ca: CrossApplication): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(ca.js, None), ClasspathDependency(ca.jvm, None))
    }

    implicit def crossProjectDependency(cp: CrossProject): CrossApplicationDependencies = {
      CrossApplicationDependencies(ClasspathDependency(cp.js, None), ClasspathDependency(cp.jvm, None))
    }
  }
}