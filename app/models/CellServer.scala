package models

import akka.actor.{Props, Actor, ActorRef}
import play.libs.Akka

import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

class CellServer extends Actor {
  var cells = Map.empty[String, ActorRef]
  var cellNo = 0

  Akka.system.scheduler.schedule(0 seconds, 200 milliseconds) {
    cells.foreach( _._2 ! Update() )
  }

  def receive = {

    case NewEnv(envName: String, envDNA : DNA) =>
      (0 until Environment.noCellsPerEnv).foreach{ _ =>
        val cellName = ("cell"+cellNo)
        cellNo += 1
        val cellActorRef = context.actorOf(Props(new Cell(cellName)), name = cellName)
        cells = cells + ( cellName -> cellActorRef )
        cellActorRef ! NewEnv(envName, envDNA)
      }

  }

}
