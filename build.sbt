name := "Game"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

resolvers += "stephenjudkins-bintray" at "http://dl.bintray.com/stephenjudkins/maven"

//libraryDependencies += "ps.tricerato" %% "babysteps/pureimage" % "0.1.2"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.2"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.7a"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.11+"

libraryDependencies += "com.nicta" %% "rng" % "1.3.0"


