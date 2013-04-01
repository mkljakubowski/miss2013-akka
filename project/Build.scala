import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "CreatureLife"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.typesafe.akka" % "akka-transactor" % "2.1-M1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      coffeescriptOptions := Seq("bare")
    )

}
