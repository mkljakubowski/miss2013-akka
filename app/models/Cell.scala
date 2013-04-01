package models

import akka.actor.{ActorRef, Actor}
import akka.transactor.Coordinated
import util.Random
import scala.math._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.stm._


class Cell(name: String, initEnergy: Int = 50) extends Actor {
  implicit val timeout = Timeout(1 second)
  var dna: DNA = DNA()
  var targetDNA: DNA = null

  def withDna(newDna: DNA): Cell = {
    dna = newDna
    this
  }

  def withPosition(newPos: Position): Cell = {
    pos = new Position(newPos.x, newPos.y)
    this
  }

  def fitness = dna.distance(targetDNA)

  var pos: Position = new Position(Random.nextDouble() * 200 - 100, Random.nextDouble() * 100 - 50)
  var envId: String = ""
  var energy = initEnergy
  var energyStm = Ref(0)
  var speed = 0.0
  var direction = Random.nextDouble() * Pi * 2
  var localEnvironment: ActorRef = null
  val masterServer: ActorRef = context.actorFor("../..")
  val energyLimit = 10

  def receive = {

    case NewEnv(envName: String, envDNA: DNA) =>
      targetDNA = envDNA
      envId = envName
      localEnvironment = context.actorFor("../../" + envName)
      localEnvironment ! Register(context.self.path.name, pos, energy, dna)

    case Update() =>
      updatePosition()
      mutatation()
      localEnvironment ! UpdateCell(name, pos, energy, dna)

    case SuckEnergy(targetCell) =>
      context.actorFor("../" + targetCell) ! SuckEnergyRequest(fitness)

    case SuckEnergySuccess => {
      increaseEnergy()
    }

    //        TODO: transactor try
    //    case coordinated@Coordinated(SuckEnergySuccess) => {
    //      energyStm = Ref(energy)
    //      coordinated atomic {
    //        implicit t =>
    //          energyStm.set(energyStm.get + 10)
    //      }
    //      increaseEnergy(energyStm.single.get)
    //  }

    case SuckEnergyRequest(otherFitness) =>
      if (otherFitness < fitness) {
        //lower fitness == better fitness
        println("SuckEnergyRequest!" + sender)

        //        TODO: transactor try
        //        implicit val timeout = Timeout(5 seconds)
        //        val coordinated: Coordinated = Coordinated()
        //        sender ! coordinated(SuckEnergySuccess)
        //        energyStm = Ref(energy)
        //        coordinated atomic {
        //          implicit t =>
        //            energyStm.set(energyStm.get - 10)
        //        }
        //        decreaseEnergy(energyStm.single.get)

        decreaseEnergy()
        sender ! SuckEnergySuccess
      }
      else {
        //if I have better fitness let other guy decrease its energy first. its better to have less energy that to have it too much
        sender ! SuckEnergyRequest(fitness)
      }
  }


  def mutatation() {
    if (scala.math.random < 0.01) {
      dna = DNA.mutate(dna)
    }
  }

  def updatePosition() = {
    val x = (pos.x + sin(direction) * speed)
    val y = (pos.y + cos(direction) * speed)
    pos = new Position(x, y)
    speed += Random.nextDouble() - 0.5
    direction += Random.nextDouble() - 0.5
    if (outOfBoundries) direction -= Pi
  }

  def outOfBoundries: Boolean =
    pos.x > Environment.screenSize.x || pos.x < -Environment.screenSize.x || pos.y < -Environment.screenSize.y || pos.y > Environment.screenSize.y


  def switchEnv() = {
    (masterServer ? AnotherEnv(localEnvironment.path.name)).map {
      case newEnvName: String if (localEnvironment.path.name != newEnvName) =>
        localEnvironment ! Unregister(context.self.path.name)
        localEnvironment = context.actorFor("../../" + newEnvName)
        localEnvironment ! Register(context.self.path.name, pos, energy, dna)
      case _ =>
    }
  }

  def dead: Receive = {
    case _ ⇒ println("ZOMBIE")
  }

  import context.become

  val energyStep = 10

//  def decreaseEnergy(newEnergy: Int) {
//    energy = newEnergy
//    if (energy <= 0) {
//      become(dead)
//      localEnvironment ! Unregister(context.self.path.name)
//      context.stop(self)
//    }
//  }

  def decreaseEnergy() {
    energy -= energyStep
    if (energy <= 0) {
      become(dead)
      localEnvironment ! Unregister(context.self.path.name)
      context.stop(self)
    }
  }

  def increaseEnergy() {
    energy += energyStep
    if (energy > energyLimit) {
      var oldEnergy = energy
      energy = energy / 2
      println("Old energy: " + oldEnergy + ", newEnergy1: " + energy)
      context.actorFor("..") ! NewCell(envId, targetDNA, dna, oldEnergy - energy, pos)
    }
  }

//  def increaseEnergy(newEnergy: Int) {
//    energy = newEnergy
//    if (energy > energyLimit) {
//      var oldEnergy = energy
//      energy = energy / 2
//      println("Old energy: " + oldEnergy + ", newEnergy1: " + energy)
//      context.actorFor("..") ! NewCell(envId, targetDNA, dna, oldEnergy - energy, pos)
//    }
//  }
}


