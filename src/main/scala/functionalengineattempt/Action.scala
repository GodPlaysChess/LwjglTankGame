package functionalengineattempt

import scala.swing.event.Key

sealed trait Action
sealed trait KeyboardAction extends Action
case class Keypress(keyCode: Key.Value) extends KeyboardAction
sealed trait MouseAction extends Action