package models.cell

import models.{NewCell, Unregister}

sealed trait EnergyLoss
case class Die(energyLost: Int) extends EnergyLoss
case class LiveOn(energyLost: Int) extends EnergyLoss

sealed trait EnergyGain
case class Split(childEnergy: Int) extends EnergyGain
case class Gain(ignore: Int = 0) extends EnergyGain

trait EnergyContainer {

  def getEnergy: Int
  def setEnergy(newEnergy: Int)

  def decreaseEnergy(): EnergyLoss = {
    val oldEnergy = getEnergy
    if (oldEnergy <= EnergyContainer.energyDecreaseStep) {
      setEnergy(0)
      Die(energyLost = oldEnergy)
    } else {
      setEnergy(oldEnergy - EnergyContainer.energyDecreaseStep)
      LiveOn(energyLost = EnergyContainer.energyDecreaseStep)
    }
  }

  def increaseEnergy(energyGained: Int): EnergyGain = {
    val oldEnergy = getEnergy
    val newEnergy = oldEnergy + energyGained
    if (newEnergy > EnergyContainer.energyLimit) {
      val energies = splitHighEnergy(oldEnergy)
      setEnergy(energies._1)
      Split(energies._2)
    } else {
      Gain()
    }
  }

  private def splitHighEnergy(highEnergy: Int) = (highEnergy / 2, highEnergy - (highEnergy / 2))
}

object EnergyContainer {
  val energyDecreaseStep = 10
  val energyLimit = 100
}