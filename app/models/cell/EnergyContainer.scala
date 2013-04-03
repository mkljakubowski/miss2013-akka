package models.cell

object EnergyContainer {
  val energyDecreaseStep = 10
  val energyLimit = 100
}


trait EnergyContainer {

  var energy: Int

  def die(energyLost: Int) : Int
  def liveOn(energyLost: Int) : Int
  def split(childEnergy: Int)
  def gain()

  def decreaseEnergy(decreaseStep: Int = EnergyContainer.energyDecreaseStep): Int = {
    if (energy <= EnergyContainer.energyDecreaseStep) {
      energy = 0
      die(energy)
    } else {
      energy -= decreaseStep
      liveOn(decreaseStep)
    }
  }

  def increaseEnergy(energyGained: Int)= {
    energy += energyGained
    if (energy > EnergyContainer.energyLimit) {
      val energies = splitHighEnergy(energy)
      energy = energies._1
      split(energies._2)
    } else {
      gain()
    }
  }

  private def splitHighEnergy(highEnergy: Int) = (highEnergy / 2, highEnergy - (highEnergy / 2))

}
