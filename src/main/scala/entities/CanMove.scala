package entities

import mech.Vec

trait CanMove extends Drawable {

  def center_=(v: Vec): Unit

  def speed: Double

  def up() = center = center - Vec(0, speed)

  def down() = center = center + Vec(0, speed)

  def left() = center = center - Vec(speed, 0)

  def right() =center = center + Vec(speed, 0)

  def move(speed: Vec) = center = center + speed

}