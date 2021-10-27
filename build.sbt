name := "Calcite"

version := "0.1"

scalaVersion := "2.13.6"

val root = project
  .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.calcite" % "calcite-core" % "1.26.0"
    )
  )