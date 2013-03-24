package models

import akka.actor.{ActorRef, Actor, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import play.api.libs.json._
import play.libs.Akka
import javax.print.attribute.standard.MediaSize.Other

object EnvServer {
  implicit val timeout = Timeout(1 second)
  lazy val serv = Akka.system.actorOf(Props[EnvServer])

  def join(): scala.concurrent.Future[(Iteratee[JsValue,_], Enumerator[JsValue])] = {
    (serv ? GetUniqueId()).map {
      case envId: Int =>
        val envName   = envId.toString
        val enumerator = Concurrent.unicast[JsValue](serv ! Join(envName, _))
        val iteratee   = Iteratee.foreach[JsValue](serv ! Event(envName, _)).mapDone(_ => serv ! Quit(envName))
        (iteratee, enumerator)

      case _ =>
        val iteratee   = Done[JsValue,Unit]((),Input.EOF)
        val enumerator =
          Enumerator[JsValue](Json.obj("error" -> "Server cannot create unique user name")) >>>
            Enumerator.enumInput(Input.EOF)
        (iteratee,enumerator)
    }
  }
}

class EnvServer extends Actor {
  var environments = Map.empty[String, ActorRef]
  val cellServ = context.actorOf(Props[CellServer])
  var id: Int = 0

  Akka.system.scheduler.schedule(0 seconds, 250 milliseconds) {
    environments.foreach( _._2 ! UpdateEnv() )
  }

  def receive = {
    case GetUniqueId() =>
      sender ! {id += 1; id}

    case Join(envName, channel) =>
      val envActorRef = context.actorOf(Props(new Environment(envName, channel)), name = envName)
      environments = environments + (envName -> envActorRef)
      cellServ ! NewEnv(envName)

//    case Event(username, event) =>
//      def getPos(axis: String) = (event \ axis).asOpt[Int]
//
//      (getPos("x"), getPos("y"), getPos("z")) match {
//        case (Some(x), Some(y), Some(z)) =>
//          val move = Move(username, Position(x, y, z))
//          players.values.foreach(_ ! move)
//          squares ! move
//
//        case _ =>
//          play.Logger.warn("Unable to parse message %s from user %s".format(event.toString(), username))
//      }
//
//    case Quit(username) =>
//      players = players - username
//
//    case otherMsg =>
//      players.values.foreach(_ ! otherMsg)
  }
}
