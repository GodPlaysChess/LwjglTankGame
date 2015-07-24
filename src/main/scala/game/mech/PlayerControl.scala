package game.mech

import game.entities.CanMove

class PlayerControl(player: CanMove) {
  private[this] var lastKey: String = ""

  def left() = player.left()
  
  def right() = player.right()

  def up() = player.up()
  
  def down() = player.down()
  
  def space() {
    lastKey = "space"
  }
  
  def last: String = lastKey
}
