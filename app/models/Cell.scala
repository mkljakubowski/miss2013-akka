package models

import akka.actor.Actor
import util.Random

class Cell extends Actor {
  val r = Random.nextInt(255)
  val g = Random.nextInt(255)
  val b = Random.nextInt(255)
  var fit : Int = 0
  var x : Double = Random.nextInt(800)
  var y : Double = Random.nextInt(450)
  var envId : Int = 0

  def receive = {

  }
}
