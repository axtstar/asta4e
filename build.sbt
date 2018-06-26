name := "asta4e"
version := "0.0.1"
scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.apache.poi/poi
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",

  "junit" % "junit" % "4.12" % Test,
  "org.specs2" %% "specs2-core" % "4.2.0" % Test,
  "org.specs2" %% "specs2-junit" % "4.3.0-9613e1025-20180617171339" % Test
)

