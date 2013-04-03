package models.cell

import models.Position
import math._

trait EvolvingCreature {

  var dna: DNA
  var environmentIdealDna: DNA

  def mutate() {
    if (random < EvolvingCreature.mutationProbability) {
      dna = DNA.mutate(dna)
    }
  }

  def fitness: Double = EvolvingCreature.maxDistance - (dna.distance(environmentIdealDna))
}

object EvolvingCreature {
  val mutationProbability = 0.01
  val maxDistance = DNA(255,255,255).distance(DNA(0,0,0))
}
