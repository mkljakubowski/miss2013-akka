package models

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import scala.math.pow


sealed abstract class ServerMessage

//quickpath
case class Join(username: String, channel: Concurrent.Channel[JsValue]) extends ServerMessage

case class Event(username: String, event: JsValue) extends ServerMessage

//case class Move         (username: String, position: Position)                    extends ServerMessage
//case class Collision    (username: String, position: Position)                    extends ServerMessage
//case class UpdateScore  (username: String, score: Int)                            extends ServerMessage
case class Quit(username: String) extends ServerMessage

//case class PathClosed   (trail: List[Position])                                   extends ServerMessage
//case class UpdateSquare (id: Int, size: Double, x: Int, y: Int, color: Int)       extends ServerMessage
//case class RemoveSquare (id: Int)                                                 extends ServerMessage
//case class IncreaseScore(bonus: Int)                                              extends ServerMessage
//case class UpdateSquares()                                                        extends ServerMessage

//creatures
case class GetUniqueId() extends ServerMessage

case class Update() extends ServerMessage

case class NewEnv(envName: String, envDNA: DNA) extends ServerMessage

case class UpdateCell(cellName: String, pos: Position, energy: Double, dna: DNA) extends ServerMessage

case class Register(cellName: String, pos: Position, energy: Double, dna: DNA) extends ServerMessage

case class Unregister(cellName: String) extends ServerMessage

case class AnotherEnv(envName: String) extends ServerMessage

case class SuckEnergy(cellName: String) extends ServerMessage

//cell that wants to suck energy sends it to its potentail prey
case class SuckEnergyRequest(fitness: Double) extends ServerMessage

//if prey cell has lower fitness success is returned
case class SuckEnergySuccess() extends ServerMessage

//else fail is returned, receiving cell is a victim
case class SuckEnergyFail() extends ServerMessage
