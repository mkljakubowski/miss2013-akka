package models.cell

object EnergyContainer {
  val energyDecreaseStep = 10
  val energyLimit = 100
}

sealed trait EnergyLoss
case class Die(energyLost: Int) extends EnergyLoss
case class LiveOn(energyLost: Int) extends EnergyLoss

sealed trait EnergyGain
case class Split(childEnergy: Int) extends EnergyGain
case class Gain(ignore: Int = 0) extends EnergyGain

trait EnergyContainer {

  var energy: Int

  def decreaseEnergy(decreaseStep: Int = EnergyContainer.energyDecreaseStep): EnergyLoss = {
    if (energy <= EnergyContainer.energyDecreaseStep) {
      val a = Die(energy)
      energy = 0
      a
    } else {
      energy -= decreaseStep
      LiveOn(decreaseStep)
    }
  }

  def increaseEnergy(energyGained: Int): EnergyGain = {
    energy += energyGained
    if (energy > EnergyContainer.energyLimit) {
      val energies = splitHighEnergy(energy)
//      println(energy + ":" + energies._1 + "+" + energies._2)
      energy = energies._1
      Split(energies._2)
    } else {
      Gain()
    }
  }

  private def splitHighEnergy(highEnergy: Int) = (highEnergy / 2, highEnergy - (highEnergy / 2))
}
