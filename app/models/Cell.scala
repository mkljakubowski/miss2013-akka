package models

import akka.actor.{ActorRef, Actor}
import util.Random
import scala.math._

class Cell(name : String) extends Actor {
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
}
