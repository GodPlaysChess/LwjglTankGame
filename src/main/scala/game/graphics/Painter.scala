package game.graphics

import java.awt.Color

import game.entities.{Drawable, Square}

import scala.swing.Graphics2D

object Painter {
  private[this] implicit def dToInt(d: Double): Int = d.toInt

  def draw(e: Drawable): Graphics2D => Unit = g => {
    e match {
      case Square(centre, size) => {
        g.setColor(Color.magenta)
        g.fillRect(centre.x - size / 2, centre.y - size / 2, size, size)
      }

    }
  }

}
