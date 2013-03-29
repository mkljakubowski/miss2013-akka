package models

import util.Random

class DNA {
  var r = Random.nextInt(255)
  var g = Random.nextInt(255)
  var b = Random.nextInt(255)

  def randomize : DNA = {
    r = Random.nextInt(255)
    g = Random.nextInt(255)
    b = Random.nextInt(255)
    this
  }

  def -(env:DNA): Int = {
    (env.r - r) + (env.g - g) + (env.b - b)
  }
}

object DNA {
  def apply() : DNA = new DNA().randomize
}