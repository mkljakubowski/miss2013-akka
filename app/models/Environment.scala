package models

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{Json, JsValue}

object Environment {
  val noCellsPerEnv = 20
  val screenSize = new Position(100, 50)
}

class Environment(envName: String, channel: Concurrent.Channel[JsValue], targetDNA : DNA) extends Actor {
  var cells = Map.empty[String, (ActorRef, Position)]

  def receive = {

    case UpdateCell(cellName, pos, energy) =>
      channel.push(Json.obj(
        "type" -> "UpdateCell", 
        "cellName" -> cellName, 
        "x" -> pos.x, 
        "y" -> pos.y, 
        "energy" -> energy))

    case Register(cellName, pos, energy, dna) =>
      channel.push(Json.obj(
        "type" -> "Register", 
        "cellName" -> cellName, 
        "x" -> pos.x, 
        "y" -> pos.y, 
        "energy" -> energy, 
        "dna" -> dna.asJSON()))
      cells = cells + (cellName -> (context.actorFor("../cellSrv/"+cellName), pos) )

    case Unregister(cellName) =>
      channel.push(Json.obj("type" -> "Unregister", "cellName" -> cellName))
      cells = cells - cellName

    case "kill" =>
      cells.foreach{ _._2._1 ! PoisonPill }
      context.self ! PoisonPill
  }

  def checkCollisions(cellName : String) = {
    cells.get(cellName).map { cell =>
      cells.filter( _._1 != cellName).foreach { otherCell =>
        if (cell._2 isNear otherCell._2._2) {
          //colliding
          ???
        }
      }
    }
  }

//  override def postStop() = println("stopped " + envName)

}
