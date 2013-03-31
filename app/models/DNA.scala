package models

import math.random
import scala.math.abs
import play.api.libs.json.{Json, JsValue}

case class DNA(r: Double, g: Double, b: Double) {

  def -(env:DNA): Double = {
    abs((env.r - r)) + abs((env.g - g)) + abs((env.b - b))
  }

  def asJSON() : JsValue =
    Json.obj(
      "r" -> r,
      "g" -> g,
      "b" -> b)
}

object DNA {
  def apply() : DNA = DNA(random,random,random)

  def cross(parentA: DNA, parentB: DNA) = DNA(parentA.r,parentB.g,parentA.b)

  def mutate(sourceDna: DNA): DNA = (random*3).toInt match {
    case 0 => DNA(random,sourceDna.g, sourceDna.b)
    case 1 => DNA(sourceDna.r,random,sourceDna.b)
    case 2 => DNA(sourceDna.r,sourceDna.g,random)
  }


}