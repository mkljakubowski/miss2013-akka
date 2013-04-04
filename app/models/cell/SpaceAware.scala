package models.cell

import scala.math._
import util.Random
import models.{Environment, Position}

trait SpaceAware {

  var position: Position
  var speed = 0.0
  var direction = random * Pi * 2
//  var direction = 0.0

  def move() = {
    val x = (position.x + sin(direction) * speed)
    val y = (position.y + cos(direction) * speed)
    position = Position(x, y)

    speed += random - 0.5
    if (speed < 0)
      speed = -speed
    if (speed > 10)
      speed -= 5

    direction += random - 0.5
    direction %= 2*Pi

    if (position.x > Environment.screenSize.x){
      direction -= Pi/2
      direction += 2*Pi
      direction += 3*Pi/2
    }else if (position.x < -Environment.screenSize.x){
      direction -= 3*Pi/2
      direction += 2*Pi
      direction += Pi/2
    }else if (position.y < -Environment.screenSize.y){
      direction += 2*Pi
      direction += Pi
    }else if (position.y > Environment.screenSize.y){
      direction -= Pi
      direction += 2*Pi
    }

    if(outOfBoundries)
      position = Position(0,0)
  }

  private def outOfBoundries: Boolean = {
    (position.x > Environment.screenSize.x + 100
      || position.x < -Environment.screenSize.x - 100
      || position.y < -Environment.screenSize.y - 100
      || position.y > Environment.screenSize.y + 100)
  }
}
