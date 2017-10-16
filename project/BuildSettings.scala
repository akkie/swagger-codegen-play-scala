/**
 * Copyright 2016 Mohiva Organisation (license at mohiva dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import sbt.Keys._
import sbt._

////*******************************
//// Basic settings
////*******************************
object BasicSettings extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings = Seq(
    organization := "com.mohiva",
    version := "0.3.0-SNAPSHOT",
    resolvers ++= Dependencies.resolvers,
    scalaVersion := Dependencies.Versions.scalaVersion,
    crossScalaVersions := Dependencies.Versions.crossScala,
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ),
    scalacOptions in Test ~= { (options: Seq[String]) =>
      options filterNot (_ == "-Ywarn-dead-code") // Allow dead code in tests (to support using mockito).
    },
    parallelExecution in Test := true,
    javaOptions in Test ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    fork in Test := true
  )
}

////*******************************
//// ScalaDoc settings
////*******************************
object Doc extends AutoPlugin {

  override def projectSettings = Seq(
    autoAPIMappings := true,
    apiMappings ++= {
      implicit val cp = (fullClasspath in Compile).value
      Map(
        scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"),
        jarFor("com.typesafe.play", "play") -> url(s"http://www.playframework.com/documentation/${Dependencies.Library.Play.version}/api/scala/"),
        jarFor("joda-time", "joda-time") -> url(s"http://joda-time.sourceforge.net/apidocs/")
      )
    }
  )

  /**
   * Gets the JAR file for a package.
   *
   * @param organization The organization name.
   * @param name The name of the package.
   * @param cp The class path.
   * @return The file which points to the JAR.
   * @see http://stackoverflow.com/a/20919304/2153190
   */
  private def jarFor(organization: String, name: String)(implicit cp: Seq[Attributed[File]]): File = {
    (for {
      entry <- cp
      module <- entry.get(moduleID.key)
      if module.organization == organization
      if module.name.startsWith(name)
      jarFile = entry.data
    } yield jarFile).head
  }
}
