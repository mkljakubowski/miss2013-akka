package models

import akka.actor.Actor
import util.Random
import scala.math._

class Cell extends Actor {
  val dna : DNA = DNA()
  var targetDNA : DNA = null
  def fitness = dna - targetDNA
  var pos : Position = new Position(Random.nextDouble()*800, Random.nextDouble()*450)
  var envId : String = ""
  var energy = 50
  var speed = 0.0
  var direction = Random.nextDouble() * Pi * 2

  def receive = {
    case NewEnv(envName: String, envDNA : DNA) =>
      targetDNA = envDNA
      envId = envName
    case Update() => {
      updatePosition()
    }
  }

  def updatePosition() = {
    val x = (pos.x + sin(direction) * speed) % Environment.screenSize.x
    val y = (pos.y + cos(direction) * speed) % Environment.screenSize.y
    pos = new Position(x, y)
    speed += Random.nextDouble()
    direction += Random.nextDouble()
  }
}
