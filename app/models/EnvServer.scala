package models

import akka.actor.{PoisonPill, ActorRef, Actor, Props}
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
        val envName :String = envId.toString
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
  var cellSrv = context.actorOf(Props(new CellServer()), name = "cellSrv")
  var id: Int = 0

  def receive = {
    case GetUniqueId() =>
      sender ! {id += 1; id}

    case Join(envName, channel) =>
      val newDNA = DNA()
      val envActorRef = context.actorOf(Props(new Environment(envName, channel, newDNA)), name = envName)
      environments = environments + (envName -> envActorRef)
      cellSrv ! NewEnv(envName, newDNA)

    case Quit(envName) =>
      environments.get(envName).map( _ ! "kill")
      environments = environments - envName

    case otherMsg =>
      environments.values.foreach(_ ! otherMsg)
  }
}
