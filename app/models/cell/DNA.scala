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

}

object DNA {
  def apply() : DNA = DNA(random,random,random)

  def cross(parentA: DNA, parentB: DNA) = DNA(
    (parentA.r + parentB.r) / 2 + random/50,
    (parentA.g + parentB.g) / 2 + random/50,
    (parentA.b + parentB.b) / 2 + random/50)

  def mutate(sourceDna: DNA) = cross(sourceDna, sourceDna)

  def mutateColor(old: Double) = (old + random)/2

  implicit def dnaJSON(dna: DNA): Json.JsValueWrapper =
    Json.obj("r" -> dna.r, "g" -> dna.g, "b" -> dna.b)

}