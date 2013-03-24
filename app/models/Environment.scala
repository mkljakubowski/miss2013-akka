package models

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue

class Environment(username: String, channel: Concurrent.Channel[JsValue]) extends Actor {
  def receive = {

  }
}
