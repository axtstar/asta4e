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

scalaVersion := "2.11.7"

assemblyJarName in assembly := { s"${name.value}-${version.value}.jar" }

val scalaMajorVersion = if (scalaVersion.toString().startsWith("2.11")){"2.11"}else{"2.12"}

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.poi/poi
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",
  "com.chuusai" % s"shapeless_2.11" % "2.3.3",

  "junit" % "junit" % "4.12" % Test,
  "org.specs2" % s"specs2-core_2.11" % "4.2.0" % Test,
  "org.specs2" % s"specs2-junit_2.11" % "4.2.0" % Test
)

publishTo := {
  if (isSnapshot.value)
    Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

