package models

import math._


case class Position(x: Double, y: Double, z: Double) {

  def isNear(other: Position): Boolean = Position.shortDistance > euclidanDist(other)

  def euclidanDist(other: Position): Double = {
    sqrt(pow((this.x - other.x), 2) + pow((this.y - other.y), 2) + pow((this.z - other.z), 2))
  }
}

object Position {

  def apply(): Position = Position(random * 200 - 100, random * 100 - 50)

  def apply(x: Double, y: Double): Position = new Position(x, y, 0)

  def apply(position: Position): Position = new Position(position.x,position.y,position.z)

  val shortDistance = 10.0
}
