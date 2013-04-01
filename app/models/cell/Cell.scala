package models.cell

import akka.actor.{ActorRef, Actor}
import scala.math._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import models._
import models.Unregister
import models.Update
import models.Register
import models.SuckEnergy
import models.AnotherEnv
import models.SuckEnergySuccess
import models.SuckEnergyRequest
import models.NewEnv
import models.UpdateCell


class Cell(initEnergy: Int = 50, initDna: DNA = DNA(), initPos: Position = Position()) extends Actor
with SpaceAware
with EvolvingCreature
with EnergyContainer {

  implicit val timeout = Timeout(1 second)

  var position: Position = initPos //SpaceAware
  var energy = initEnergy // EnergyContainer
  var dna: DNA = initDna //EvolvingCreature

  var environmentIdealDna: DNA = null
  var localEnvironment: ActorRef = null
  val masterServer: ActorRef = context.actorFor("../..")

  def receive = {
    case NewEnv(enviroment: ActorRef, envDna: DNA) =>
      environmentIdealDna = envDna
      localEnvironment = enviroment
      localEnvironment ! Register(context.self.path.name, position, energy, getDna)

    case Update() =>
      move()
      mutate()
      localEnvironment ! UpdateCell(context.self.path.name, position, energy, dna)

    case SuckEnergy(targetCell) =>
      context.actorFor("../" + targetCell) ! SuckEnergyRequest(fitness)

    case SuckEnergySuccess(energyTransfered: Int) => {
      increaseEnergy_(energyTransfered)
    }

    case SuckEnergyRequest(otherFitness) =>
      if (otherFitness > fitness) {
        val energySucked = decreaseEnergy_()
        sender ! SuckEnergySuccess(energySucked)
      }
      else if (otherFitness > fitness) {
        sender ! SuckEnergyRequest(fitness)
      }
  }

  //Evolving Creature
  override def getIdealDna: DNA = environmentIdealDna

  override def getDna: DNA = dna

  override def setDna(newDna: DNA) = {
    dna = newDna
  }

  //Space Aware
  override def getPosition: Position = position

  override def setPosition(newPosition: Position) = {
    position = newPosition
  }

  //Energy Container
  def getEnergy: Int = energy

  def setEnergy(newEnergy: Int) = {
    energy = newEnergy
  }

  def switchEnv() = {
    (masterServer ? AnotherEnv(localEnvironment.path.name)).map {
      case newEnvName: String if (localEnvironment.path.name != newEnvName) =>
        localEnvironment ! Unregister(context.self.path.name)
        localEnvironment = context.actorFor("../../" + newEnvName)
        localEnvironment ! Register(context.self.path.name, position, energy, dna)
      case _ =>
    }
  }

  def dead: Receive = {
    case _ ⇒ println("ZOMBIE")
  }

  import context.become

  def decreaseEnergy_(): Int = {
    decreaseEnergy() match {
      case Die(energyLost: Int) => {
        become(dead)
        localEnvironment ! Unregister(context.self.path.name)
        context.stop(self)
        energyLost
      }
      case LiveOn(energyLost: Int) => energyLost
    }
  }

  def increaseEnergy_(energyGained: Int) {
    increaseEnergy(energyGained) match {
      case Gain(_) => {}
      case Split(energyGivenAway: Int) => {
        context.actorFor("..") ! NewCell(localEnvironment, environmentIdealDna, dna, energyGivenAway, position)
      }
    }
  }

}


