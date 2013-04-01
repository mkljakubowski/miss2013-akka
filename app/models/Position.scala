package models

import math._


case class Position(x: Double, y: Double, z: Double) {

  def this(x: Double, y: Double) = this(x, y, 0.0)

  def isNear(other: Position): Boolean = Position.shortDistance > sqrt(
    pow((this.x - other.x), 2) + pow((this.y - other.y), 2) + pow((this.z - other.z), 2))
}

object Position{
  val shortDistance = 10.0
}
