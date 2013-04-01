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

  def decreaseEnergy(decreaseStep: Int = EnergyContainer.energyDecreaseStep): EnergyLoss = {
    val oldEnergy = getEnergy
    if (oldEnergy <= EnergyContainer.energyDecreaseStep) {
      setEnergy(0)
      Die(energyLost = oldEnergy)
    } else {
      setEnergy(oldEnergy - decreaseStep)
      LiveOn(energyLost = decreaseStep)
    }
  }

  def increaseEnergy(energyGained: Int): EnergyGain = {
    val newEnergy = getEnergy + energyGained
    if (newEnergy > EnergyContainer.energyLimit) {
      val energies = splitHighEnergy(newEnergy)
      println(newEnergy + ":" + energies._1 + "+" + energies._2)
      setEnergy(energies._1)
      Split(energies._2)
    } else {
      setEnergy(newEnergy)
      Gain()
    }
  }

  private def splitHighEnergy(highEnergy: Int) = (highEnergy / 2, highEnergy - (highEnergy / 2))
}

object EnergyContainer {
  val energyDecreaseStep = 10
  val energyLimit = 100
}