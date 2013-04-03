package models.cell

import scala.math._
import util.Random
import models.{Environment, Position}

trait SpaceAware {

  var position: Position
  var speed = 0.0
  var direction = random * Pi * 2

  def move() = {
    updatePosition()
  }

  private def updatePosition() = {
    val x = (position.x + sin(direction) * speed)
    val y = (position.y + cos(direction) * speed)
    position = Position(x, y)

    speed += random - 0.5
    direction += random - 0.5
    if (outOfBoundries) direction -= Pi
  }

  private def outOfBoundries: Boolean = {
    (position.x > Environment.screenSize.x
      || position.x < -Environment.screenSize.x
      || position.y < -Environment.screenSize.y
      || position.y > Environment.screenSize.y)
  }


}
