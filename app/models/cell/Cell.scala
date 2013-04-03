package models.cell

import akka.actor.{ActorRef, Actor}
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
import concurrent.{Await, Future}


class Cell(initEnergy: Int = 50, initDna: DNA = DNA(), initPos: Position = Position()) extends Actor
with SpaceAware
with EvolvingCreature
with EnergyContainer {

  implicit val timeout = Timeout(1 second)

  var position: Position = initPos
  var energy = initEnergy
  var dna: DNA = initDna

  var environmentIdealDna: DNA = null
  var localEnvironment: ActorRef = null
  val masterServer: ActorRef = context.actorFor("../..")
  val cellServer: ActorRef = context.actorFor("..")

  def receive = {
    case NewEnv(enviroment: ActorRef, envDna: DNA) =>
      environmentIdealDna = envDna
      localEnvironment = enviroment
      localEnvironment ! Register(context.self.path.name, position, energy, dna)

    case Update() =>
      if (teleport) {
        switchEnv()
      } else {
        move()
        mutate()
        localEnvironment ! UpdateCell(context.self.path.name, position, energy, dna)
      }

    case SuckEnergy(targetCell) =>
      targetCell ! SuckEnergyRequest(fitness)

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

    //Send from Enviroment
    case Copulate(otherCell: ActorRef) => {
      if (enoughEnergy) {
        otherCell ! CopulateRequest
      }
    }

    //Send from Cell
    case CopulateRequest => {
      if (enoughEnergy) {
        //TODO there's problem when our mate is dead before spawning child, let it for now
        //TODO eventually maybe there should be CopulatingCoordinatingActor that would use transactor to decrease energy of both parents in single transaction
        //TODO but now let's stick to simple. naive solution
        decreaseEnergy(25)
        sender ! CopulateSuccess(dna)
      }
    }

    //Send fro Cell
    case CopulateSuccess(otherDna: DNA) => {
      //TODO other cell gave its portion of energy, the problem is that we might no have enough of it now
      decreaseEnergy(25)
      spawnChild(otherDna)
    }

  }

  def teleport = scala.math.random < 0.1

  def enoughEnergy = energy > 75

  def spawnChild(otherDna: DNA) = {
    val childDna = DNA.cross(dna, otherDna)
//    println("Spawn Child")
    cellServer ! NewCell(localEnvironment, environmentIdealDna, childDna, 50, position)
  }

  def switchEnv() = {
    val future: Future[Any] = masterServer ? AnotherEnv(localEnvironment.path.name)
    val result: Any = Await.result(future, 1.second)
    result match {
      case newEnvName: String if (localEnvironment.path.name != newEnvName) =>
        localEnvironment ! Unregister(context.self.path.name)
        localEnvironment = context.actorFor("../../" + newEnvName)
        localEnvironment ! Register(context.self.path.name, position, energy, dna)
        println("Teleport!")
      case _ =>
    }

  }

  def dead: Receive = {
    case _ ⇒ println("ZOMBIE")
  }

  def decreaseEnergy_(): Int = {
    decreaseEnergy() match {
      case Die(energyLost: Int) => {
        context.become(dead)
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
        cellServer ! NewCell(localEnvironment, environmentIdealDna, dna, energyGivenAway, position)
      }
    }
  }

}


