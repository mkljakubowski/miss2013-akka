package models

import akka.actor.{ActorRef, Actor}
import util.Random
import scala.math._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

class Cell(name : String) extends Actor {
  implicit val timeout = Timeout(1 second)
  val dna : DNA = DNA()
  var targetDNA : DNA = null
  def fitness = dna - targetDNA
  def radius = 5 + fitness
  var pos : Position = new Position(Random.nextDouble()*200 - 100, Random.nextDouble()*100 - 50)
  var envId : String = ""
  var energy = 50
  var speed = 0.0
  var direction = Random.nextDouble() * Pi * 2
  var env : ActorRef = null
  val srv : ActorRef = context.actorFor("../..")

  def receive = {

    case NewEnv(envName: String, envDNA : DNA) =>
      targetDNA = envDNA
      envId = envName
      env = context.actorFor("../../"+envName)
      env ! Register(context.self.path.name, pos, radius, dna)

    case Update() =>
      updatePosition()
      env ! UpdateCell(name, pos, radius)

  }

  def updatePosition() = {
    val x = (pos.x + sin(direction) * speed)
    val y = (pos.y + cos(direction) * speed)
    pos = new Position(x, y)
    speed += Random.nextDouble() - 0.5
    direction += Random.nextDouble() - 0.5
    if (outOfBoundries) direction -= Pi
  }

  def outOfBoundries : Boolean =
    pos.x > Environment.screenSize.x || pos.x < -Environment.screenSize.x || pos.y < -Environment.screenSize.y || pos.y > Environment.screenSize.y

//  override def postStop() = println("stopped " + context.self.path.name)

  def switchEnv() = {
    (srv ? AnotherEnv(env.path.name)).map {
      case newEnvName : String if (env.path.name != newEnvName) =>
        env ! Unregister(context.self.path.name)
        env = context.actorFor("../../"+newEnvName)
        env ! Register(context.self.path.name, pos, radius, dna)
      case _ =>
    }
  }
}
