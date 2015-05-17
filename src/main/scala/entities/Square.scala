package entities

import mech.Vec

case class Square(var center: Vec, size: Double) extends Drawable with CanMove with CanFire {

  override def speed: Double = 10

  override def space: Bullet = Bullet(center)
}