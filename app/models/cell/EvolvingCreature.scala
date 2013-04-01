package models.cell

import models.Position
import math._

trait EvolvingCreature {

  def getIdealDna: DNA
  def getDna: DNA
  def setDna(newDna: DNA)

  def mutate() {
    if (random < EvolvingCreature.mutationProbability) {
      setDna(DNA.mutate(getDna))
    }
  }

  def fitness: Double = EvolvingCreature.maxDistance - (getDna.distance(getIdealDna))
}

object EvolvingCreature {
  val mutationProbability = 0.01
  val maxDistance = DNA(255,255,255).distance(DNA(0,0,0))
}
