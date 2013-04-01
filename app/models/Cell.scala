package models

import akka.actor.{ActorRef, Actor}
import util.Random
import scala.math._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._


class Cell(initEnergy: Int = 50, initDna: DNA = DNA(), initPos: Position = Position()) extends Actor {
  implicit val timeout = Timeout(1 second)


  var dna: DNA = initDna
  var position: Position = initPos
  var energy = initEnergy

  var environmentIdealDna: DNA = null

  var speed = 0.0
  var direction = Random.nextDouble() * Pi * 2

  var localEnvironment: ActorRef = null
  val masterServer: ActorRef = context.actorFor("../..")
  val energyLimit = 100

  def fitness = dna.distance(environmentIdealDna)

  def receive = {
    case NewEnv(enviroment: ActorRef, envDna: DNA) =>
      environmentIdealDna = envDna
      localEnvironment = enviroment
      localEnvironment ! Register(context.self.path.name, position, energy, dna)
    case Update() =>
      updatePosition()
      mutate()
      localEnvironment ! UpdateCell(context.self.path.name, position, energy, dna)
    case SuckEnergy(targetCell) =>
      context.actorFor("../" + targetCell) ! SuckEnergyRequest(fitness)
    case SuckEnergySuccess(energyTransfered: Int) => {
      increaseEnergy(energyTransfered)
    }
    case SuckEnergyRequest(otherFitness) =>
      if (otherFitness < fitness) {
        val energySucked = decreaseEnergy()
        sender ! SuckEnergySuccess(energySucked)
      }
      else if (otherFitness > fitness) {
        sender ! SuckEnergyRequest(fitness)
      }
  }


  def mutate() {
    if (random < 0.01) {
      dna = DNA.mutate(dna)
    }
  }

  def updatePosition() = {
    val x = (position.x + sin(direction) * speed)
    val y = (position.y + cos(direction) * speed)
    position = Position(x, y)
    speed += Random.nextDouble() - 0.5
    direction += Random.nextDouble() - 0.5
    if (outOfBoundries) direction -= Pi
  }

  def outOfBoundries: Boolean =
    position.x > Environment.screenSize.x || position.x < -Environment.screenSize.x || position.y < -Environment.screenSize.y || position.y > Environment.screenSize.y


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

  val energyDecreaseStep = 10

  def decreaseEnergy():Int = {
    val oldEnergy = energy
    if(energy < energyDecreaseStep) {
      energy = 0
      become(dead)
      localEnvironment ! Unregister(context.self.path.name)
      context.stop(self)
    } else {
      energy -= energyDecreaseStep
    }
    oldEnergy - energy
  }

  def increaseEnergy(energyGained: Int) {
    energy += energyGained
    if (energy > energyLimit) {
      var oldEnergy = energy
      energy = energy / 2
      context.actorFor("..") ! NewCell(localEnvironment, environmentIdealDna, dna, oldEnergy - energy, position)
    }
  }
}


