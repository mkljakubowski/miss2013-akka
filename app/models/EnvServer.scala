package models

import akka.actor.{PoisonPill, ActorRef, Actor, Props}
import akka.pattern.ask
import akka.util.Timeout

import cell.DNA
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
  var envDnas = Map.empty[ActorRef,DNA]
  var cellSrv = context.actorOf(Props(new CellServer()), name = "cellSrv")
  var id: Int = 0

  def receive = {
    case GetUniqueId() =>
      sender ! {id += 1; id}

    case Join(envName, channel) =>
      val idealDna = DNA()
      val envActorRef = context.actorOf(Props(new Environment(envName, channel, idealDna)), name = envName)
      environments +=  (envName -> envActorRef)
      val tuple: (ActorRef, DNA) = envActorRef -> idealDna
      envDnas += tuple
      cellSrv ! NewEnv(envActorRef, idealDna)
      channel.push(targetDnaMessage(idealDna))

    case Quit(envName) =>
      environments.get(envName).map( _ ! "kill")
      val ref: ActorRef = environments.get(envName).get
      environments = environments - envName
      envDnas = envDnas - ref

    case AnotherEnv(envName) =>
      //Co tu się dzieje?
      val asList = environments.toList
      val leftEnvs = asList.last +: asList // (envN, env1 , env2, ...)
      val rightEnvs = asList :+ asList.head // (env1, .., envN, env1)
      val zipped = leftEnvs zip rightEnvs // ((envN,env1), (env1,env2), ...)
      sender ! zipped.filter{ _._1._1 == envName}.head._2._1

   case otherMsg =>
      environments.values.foreach(_ ! otherMsg)
  }

  def targetDnaMessage(idealDna: DNA): JsObject = {
    Json.obj(
      "type" -> "TargetDna",
      "dna" -> idealDna)
  }
}
