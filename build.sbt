scalaVersion := "2.12.2"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.9.0"
      // "io.monix" %% "monix-eval" % "2.3.0", // for Task
      // "io.monix" %% "monix-cats" % "2.3.0"  // for Task -> Cats integration
    )
  )
