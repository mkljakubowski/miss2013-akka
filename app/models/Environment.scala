package models

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{Json, JsValue}

object Environment {
  val noCellsPerEnv = 100
  val screenSize = Position(100, 50)
}

class Environment(envName: String, channel: Concurrent.Channel[JsValue], targetDNA: DNA) extends Actor {
  var cells = Map.empty[String, (ActorRef, Position)]

  def receive = {

    case UpdateCell(cellName, pos, energy, dna) =>
      channel.push(Json.obj(
        "type" -> "UpdateCell",
        "cellName" -> cellName,
        "x" -> pos.x,
        "y" -> pos.y,
        "energy" -> energy,
        "dna" -> dna.asJSON()))
      cells += (cellName ->(getActorRefForCell(cellName), pos))
      checkCollisions(cellName)

    case Register(cellName, pos, energy, dna) =>
      channel.push(Json.obj(
        "type" -> "Register",
        "cellName" -> cellName,
        "x" -> pos.x,
        "y" -> pos.y,
        "energy" -> energy,
        "dna" -> dna.asJSON()))
      cells += (cellName ->(getActorRefForCell(cellName), pos))

    case Unregister(cellName) =>
      println("Unregister")
      channel.push(Json.obj("type" -> "Unregister", "cellName" -> cellName))
      cells = cells - cellName

    case "kill" =>
      cells.foreach {
        _._2._1 ! PoisonPill
      }
      context.self ! PoisonPill
  }

  def getActorRefForCell(cellName: String): ActorRef = {
    context.actorFor("../cellSrv/" + cellName)
  }

  //  def checkCollisions(cellName: String) {
  //    if (cells.size > 2) {
  //      cells.foreach(
  //
  //      )
  //      val list: (ActorRef, Position) = cells.values.toList(1)
  ////      println(list._1)
  //      list._1 ! SuckEnergy(cellName)
  //    }
  //    else {
  //      println("Too small")
  //    }
  //  }

  def checkCollisions(cellName: String) {
    val option: Option[(ActorRef, Position)] = cells.get(cellName)
    option.map {
      cell =>
        cells.filter(_._1 != cellName)
          .foreach {
          otherCell =>
            if (cell._2 isNear otherCell._2._2) {
              cell._1 ! SuckEnergy(otherCell._1)
            }
        }
    }
  }

  //  override def postStop() = println("stopped " + envName)

}
