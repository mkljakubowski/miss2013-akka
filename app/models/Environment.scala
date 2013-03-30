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

  (0 until Environment.noCellsPerEnv).foreach{ cellNo =>
    val cellName = (envName+"cell"+cellNo)
    val cellActorRef = context.actorOf(Props(new Cell(cellName)), name = cellName)
    cells = cells + ( cellName -> cellActorRef )
    cellActorRef ! NewEnv(envName, targetDNA)
  }

  def receive = {

    case Update() =>
      cells.foreach{ _._2 ! Update() }

    case UpdateCell(cellName, pos, r, dna) =>
      channel.push(Json.obj("type" -> "UpdateCell", "cellName" -> cellName, "x" -> pos.x, "y" -> pos.y, "r" -> r, "dna" -> dna.asJSON()))

  }

}
