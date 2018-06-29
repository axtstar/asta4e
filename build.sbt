// POM settings for Sonatype
organization := "com.axtstar"
homepage := Some(url("https://github.com/axtstar/asta4e"))
scmInfo := Some(ScmInfo(url("https://github.com/axtstar/asta4e"),
"git@github.com:axtstar/asta4e.git"))
developers := List(Developer("axtstar",
  "axt",
  "axtstart@gmail.com",
  url("https://github.com/axtstar")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

name := "asta4e"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.4"

assemblyJarName in assembly := { s"${name.value}-${version.value}.jar" }

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.poi/poi
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",
  "com.chuusai" % "shapeless_2.12" % "2.3.3",

  "junit" % "junit" % "4.12" % Test,
  "org.specs2" %% "specs2-core" % "4.2.0" % Test,
  "org.specs2" %% "specs2-junit" % "4.3.0-9613e1025-20180617171339" % Test
)
