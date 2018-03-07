name := "youi-plugin"
organization := "io.youi"
version := "1.0.2"
sbtPlugin := true
crossSbtVersions := Vector("0.13.17", "1.1.0")

// Workaround for https://github.com/sbt/sbt/issues/3393
// This will be solved in sbt 0.13.17
libraryDependencies += {
  val currentSbtVersion = (sbtBinaryVersion in pluginCrossBuild).value
  Defaults.sbtPluginExtra("org.scala-js" % "sbt-scalajs" % "0.6.22", currentSbtVersion, (scalaBinaryVersion in update).value)
}

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

publishTo in ThisBuild := sonatypePublishTo.value
sonatypeProfileName in ThisBuild := "io.youi"
publishMavenStyle in ThisBuild := true
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/youi-plugin/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GithubHosting("outr", "youi-template", "matt@outr.com"))
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