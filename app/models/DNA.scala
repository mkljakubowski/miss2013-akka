package models

import util.Random
import play.api.libs.json.{Json, JsValue}

class DNA {
  var r = 0.0
  var g = 0.0
  var b = 0.0

  def randomize : DNA = {
    r = Random.nextDouble()
    g = Random.nextDouble()
    b = Random.nextDouble()
    this
  }

  def -(env:DNA): Double = {
    (env.r - r) + (env.g - g) + (env.b - b)
  }

  def asJSON() : JsValue =
    Json.obj("r" -> r, "g" -> g, "b" -> b)
}

object DNA {
  def apply() : DNA = new DNA().randomize
  def cross(parentA : DNA, parentB : DNA) : DNA = ???
  def mutate(parent : DNA) : DNA = ???
}