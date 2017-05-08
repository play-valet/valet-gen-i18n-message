lazy val root = (project in file("."))
  .settings(
    name         := "valet-gen-i18n-message",
    scalaVersion := "2.11.8",
    libraryDependencies ++= List(
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "com.typesafe" % "config" % "1.3.1",
      "commons-io" % "commons-io" % "2.5"
    )
  )
