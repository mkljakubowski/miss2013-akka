package models

import cell.DNA
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import scala.math.pow
import akka.actor.{PoisonPill, ActorRef, Actor, Props}

sealed abstract class ServerMessage

//quickpath
case class Join(username: String, channel: Concurrent.Channel[JsValue]) extends ServerMessage

case class Event(username: String, event: JsValue) extends ServerMessage

case class Quit(username: String) extends ServerMessage

//creatures
case class GetUniqueId() extends ServerMessage

case class Update() extends ServerMessage

case class NewEnv(environment: ActorRef, envDna: DNA) extends ServerMessage

//used when splitting cell into two. the cell sends it to CellServ, CellServ creates new cell a puts it in right Env,
case class NewCell(environment: ActorRef, envDna: DNA, dna: DNA, energy: Int, pos: Position) extends ServerMessage


case class UpdateCell(cellName: String, pos: Position, energy: Double, dna: DNA) extends ServerMessage

case class Register(cellName: String, pos: Position, energy: Double, dna: DNA) extends ServerMessage

case class Unregister(cellName: String) extends ServerMessage

case class AnotherEnv(envName: String) extends ServerMessage

case class SuckEnergy(cellName: String) extends ServerMessage

//cell that wants to suck energy sends it to its potentail prey
case class SuckEnergyRequest(fitness: Double) extends ServerMessage

//if prey cell has lower fitness success is returned
case class SuckEnergySuccess(energyTransfered: Int) extends ServerMessage

//else fail is returned, receiving cell is a victim
case class SuckEnergyFail() extends ServerMessage

