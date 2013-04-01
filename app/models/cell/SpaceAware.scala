package models.cell

import scala.math._
import util.Random
import models.{Environment, Position}

trait SpaceAware {

  var speed = 0.0
  var direction = random * Pi * 2

  def move() = {
    updatePosition()
  }

  def getPosition: Position

  def setPosition(newPosition: Position)

  private def updatePosition() = {
    val currentPosition = getPosition
    val x = (currentPosition.x + sin(direction) * speed)
    val y = (currentPosition.y + cos(direction) * speed)
    val newPosition = Position(x, y)
    setPosition(newPosition)
    speed += random - 0.5
    direction += random - 0.5
    if (outOfBoundries) direction -= Pi
  }

  private def outOfBoundries: Boolean = {
    val position = getPosition
    (position.x > Environment.screenSize.x
      || position.x < -Environment.screenSize.x
      || position.y < -Environment.screenSize.y
      || position.y > Environment.screenSize.y)
  }


}
