name := "youi-plugin"
organization := "io.youi"
version := "1.2.0"
sbtPlugin := true

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.4.0")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

publishTo in ThisBuild := sonatypePublishTo.value
sonatypeProfileName in ThisBuild := "io.youi"
publishMavenStyle in ThisBuild := true
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/youi-plugin/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "youi-template", "matt@outr.com"))
homepage in ThisBuild := Some(url("https://github.com/outr/youi-template"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/outr/youi-template"),
    "scm:git@github.com:outr/youi-template.git"
  )
)
developers in ThisBuild := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.", url=url("http://matthicks.com"))
)