package io.youi.plugin

import sbt.ClasspathDependency

case class CrossApplicationDependencies(js: ClasspathDependency,
                                        jvm: ClasspathDependency)
