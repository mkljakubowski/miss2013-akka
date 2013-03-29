package models

import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue

object Environment {
  val noCellsPerEnv = 20
  val screenSize = new Position(800, 450)
}

class Environment(envName: String, channel: Concurrent.Channel[JsValue]) extends Actor {
  var cells = Map.empty[String, ActorRef]
  val targetDNA: DNA = DNA()

  def receive = {
    case NewEnv(_, _) => {
      (0 until Environment.noCellsPerEnv).foreach{ cellNo =>
        val cellActorRef = context.actorOf(Props[Cell], "Cell")
        val cellName:String = (envName + "-" + cellNo.toString)
        cells = cells + ( cellName -> cellActorRef )
        cellActorRef ! NewEnv(envName, targetDNA)
      }
    }
    case Update() => {
      cells.foreach{ _._2 ! Update() }
    }
  }
}
