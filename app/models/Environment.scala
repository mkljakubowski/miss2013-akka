package models

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{Json, JsValue}

object Environment {
  val noCellsPerEnv = 20
  val screenSize = new Position(100, 50)
}

class Environment(envName: String, channel: Concurrent.Channel[JsValue], targetDNA : DNA) extends Actor {
  var cells = Map.empty[String, ActorRef]

  def receive = {

    case UpdateCell(cellName, pos, r) =>
      channel.push(Json.obj("type" -> "UpdateCell", "cellName" -> cellName, "x" -> pos.x, "y" -> pos.y, "r" -> r))

    case Register(cellName, pos, r, dna) =>
      channel.push(Json.obj("type" -> "Register", "cellName" -> cellName, "x" -> pos.x, "y" -> pos.y, "r" -> r, "dna" -> dna.asJSON()))
      cells = cells + (cellName -> context.actorFor("../cellSrv/"+cellName))

    case Unregister(cellName) =>
      channel.push(Json.obj("type" -> "Unregister", "cellName" -> cellName))
      cells = cells - cellName

    case "kill" =>
      cells.foreach{ _._2 ! PoisonPill }
      context.self ! PoisonPill
  }

  def checkCollisions = ???

//  override def postStop() = println("stopped " + envName)

}
