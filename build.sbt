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
version := "0.0.2"

scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.11.12", "2.12.6")

scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xfatal-warnings",  // New lines for each options
  "-unchecked"
)

assemblyJarName in assembly := { s"${name.value}-${version.value}.jar" }

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.poi/poi
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",
  "com.chuusai" %% s"shapeless" % "2.3.3",

  "org.junit.jupiter" % "junit-jupiter-api" % "5.2.0" % Test,
  "org.specs2" %% s"specs2-core" % "4.3.0" % Test,
  "org.specs2" %% s"specs2-junit" % "4.3.0" % Test
)

publishTo := {
  if (isSnapshot.value)
    Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

