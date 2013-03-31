package models

import akka.actor.{ActorRef, Actor}
import util.Random
import scala.math._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

class Cell(name: String) extends Actor {
  implicit val timeout = Timeout(1 second)
  var dna: DNA = DNA()
  var targetDNA: DNA = null

  def fitness = dna - targetDNA

  var pos: Position = new Position(Random.nextDouble() * 200 - 100, Random.nextDouble() * 100 - 50)
  var envId: String = ""
  var energy = 50
  var speed = 0.0
  var direction = Random.nextDouble() * Pi * 2
  var localEnvironment: ActorRef = null
  val masterServer: ActorRef = context.actorFor("../..")
  var isDead: Boolean = false

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
      if (!isDead) {
//        println("SuckENergy with target: " + targetCell)
        context.actorFor("../" + targetCell) ! SuckEnergyRequest(fitness)
      }

    case SuckEnergyFail => {
//      println("SuckENergyFail")
      decreaseEnergy()
    }

    case SuckEnergySuccess => increaseEnergy()


    case SuckEnergyRequest(otherFitness) =>
      if (!isDead) {
//        println("SucKENergyRequest!")
        if (otherFitness > fitness) {
          sender ! SuckEnergySuccess()
          decreaseEnergy()
        } else {
          sender ! SuckEnergyFail()
          increaseEnergy()
        }
      }


  }


  def mutatation() {
    if (scala.math.random < 0.1) {
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

  //  override def postStop() = println("stopped " + context.self.path.name)

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
  def decreaseEnergy() {
    energy -= 1
    if (energy <= 0) {
      //death
      println("ENERGY 0000000000000000000000000000000")
      isDead = true
      become(dead)
      localEnvironment ! Unregister(context.self.path.name)
      context.stop(self)
    }
  }

  def increaseEnergy() {
    energy += 1
  }
}


