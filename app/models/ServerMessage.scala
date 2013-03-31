package models

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue

case class Position(x: Double, y: Double, z: Double){
  def this(x:Double, y:Double) = this(x,y,0.0)
  def isNear(other : Position) : Boolean = 2.0 > Math.sqrt(
    Math.pow((this.x - other.x),2) + Math.pow((this.y - other.y),2) + Math.pow((this.z - other.z),2))
}

sealed abstract class ServerMessage

//quickpath
case class Join         (username: String, channel: Concurrent.Channel[JsValue])  extends ServerMessage
case class Event        (username: String, event: JsValue)                        extends ServerMessage
case class Move         (username: String, position: Position)                    extends ServerMessage
case class Collision    (username: String, position: Position)                    extends ServerMessage
case class UpdateScore  (username: String, score: Int)                            extends ServerMessage
case class Quit         (username: String)                                        extends ServerMessage
case class PathClosed   (trail: List[Position])                                   extends ServerMessage
case class UpdateSquare (id: Int, size: Double, x: Int, y: Int, color: Int)       extends ServerMessage
case class RemoveSquare (id: Int)                                                 extends ServerMessage
case class IncreaseScore(bonus: Int)                                              extends ServerMessage
case class UpdateSquares()                                                        extends ServerMessage

//creatures
case class GetUniqueId  ()                                                        extends ServerMessage
case class Update       ()                                                        extends ServerMessage
case class NewEnv       (envName: String, envDNA : DNA)                           extends ServerMessage
case class UpdateCell   (cellName: String, pos : Position, energy : Double)            extends ServerMessage
case class Register     (cellName: String, pos : Position, energy : Double, dna : DNA) extends ServerMessage
case class Unregister   (cellName: String)                                        extends ServerMessage
case class AnotherEnv   (envName: String)                                         extends ServerMessage
