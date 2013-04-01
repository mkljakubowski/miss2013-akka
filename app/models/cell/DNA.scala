package models.cell

import math.random
import scala.math.pow
import scala.math.sqrt
import play.api.libs.json.{Json, JsValue}

case class DNA(r: Double, g: Double, b: Double) {

  def distance(env:DNA): Double = {
    val a = env.r - r
    val b = env.g - g
    val c = env.b - b
    sqrt(a*a + b*b + c*c)
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
    case 0 => DNA(mutateColor(sourceDna.r),sourceDna.g, sourceDna.b)
    case 1 => DNA(sourceDna.r,mutateColor(sourceDna.g),sourceDna.b)
    case 2 => DNA(sourceDna.r,sourceDna.g,mutateColor(sourceDna.b))
  }

  def mutateColor(old: Double) = (old + random)/2


}