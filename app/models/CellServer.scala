package models

import akka.actor.{ActorRef, Props, Actor}

object CellServer {
  val noCellsPerEnv = 20
  val newCellFitness = 50
}

class CellServer extends Actor {
  var cells = Map.empty[String, List[ActorRef]].withDefaultValue(List.empty[ActorRef])

  def receive = {
    case NewEnv(envName) => {
      (0 until CellServer.noCellsPerEnv).foreach{ _ =>
        val cellActorRef =  context.actorOf(Props[Cell], "Cell")
        cells = cells.updated(envName, cellActorRef :: cells(envName))
      }
    }

  }
}
