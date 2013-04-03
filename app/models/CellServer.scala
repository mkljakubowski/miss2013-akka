package models

import akka.actor.{Props, Actor, ActorRef}
import cell.{DNA, Cell}
import play.libs.Akka

import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

class CellServer extends Actor {
  var cells = Map.empty[String, ActorRef]
  var cellNo = 0

  Akka.system.scheduler.schedule(0 seconds, 150 milliseconds) {
    cells.foreach(_._2 ! Update())
  }

  def receive = {

    case NewEnv(enviroment: ActorRef, envDNA: DNA) =>
      (0 until Environment.noCellsPerEnv).foreach {
        _ =>
          val cellName = getNextCellName
          val cellActorRef = context.actorOf(Props(new Cell()), name = cellName)
          cells += (cellName -> cellActorRef)
          cellActorRef ! NewEnv(enviroment, envDNA)
      }

    case NewCell(enviroment: ActorRef, envDna: DNA, cellDna: DNA, energy: Int, pos: Position) =>
      val cellName = getNextCellName
      val cellActorRef = context.actorOf(Props( new Cell(energy,cellDna,pos)), name = cellName)
      cells += (cellName -> cellActorRef)
      cellActorRef ! NewEnv(enviroment, envDna)

    case deadCell: String =>
      cells -= deadCell
  }

  def getNextCellName: String = {
    val cellName = ("cell" + cellNo)
    cellNo += 1
    cellName
  }
}

