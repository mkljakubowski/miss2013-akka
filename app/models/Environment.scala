package models

import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{Json, JsValue}

object Environment {
  val noCellsPerEnv = 20
  val screenSize = new Position(800, 450)
}

class Environment(envName: String, channel: Concurrent.Channel[JsValue], targetDNA : DNA) extends Actor {
  var cells = Map.empty[String, ActorRef]

  def receive = {

    case NewEnv(_, _) =>
      (0 until Environment.noCellsPerEnv).foreach{ cellNo =>
        val cellName = (envName+"cell"+cellNo)
        val cellActorRef = context.actorOf(Props(new Cell(cellName)), cellName)
        cells = cells + ( cellName -> cellActorRef )
        cellActorRef ! NewEnv(envName, targetDNA)
      }

    case Update() =>
      cells.foreach{ _._2 ! Update() }

    case UpdateCell(cellId, pos, r) =>
      channel.push(Json.obj("type" -> "UpdateCell", "cellId" -> cellId, "x" -> pos.x, "y" -> pos.y, "r" -> r))

  }
}
