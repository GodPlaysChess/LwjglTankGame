package game

import java.awt.{Color, Dimension, Graphics2D}

import game.data.World
import game.entities.Square
import game.graphics.Painter
import game.mech.{PlayerControl, Vec}

import scala.swing.event.Key._
import scala.swing.event.{FocusLost, KeyPressed, MouseClicked}
import scala.swing.{MainFrame, Panel, SimpleSwingApplication}

object SwingBasedGame extends SimpleSwingApplication {

  val player = Square(Vec(100, 200), 30)
  val entities = World(players = Vector(player))
  val control = new PlayerControl(player)

  def onKeyPress(keyCode: Value) = keyCode match {
    case Left => control.left()
    case Right => control.right()
    case Up => control.up()
    case Down => control.down()
    case Space => control.space()
    case _ =>
  }

  lazy val mainScreen = new Panel {
    // initScreen()
    background = Color.white
    preferredSize = new Dimension(640, 480)
    focusable = true

    // initIO()
    listenTo(mouse.clicks, mouse.moves, keys)
    reactions += {
      case e: MouseClicked => // fire
      case KeyPressed(_, key, _, _) =>
        onKeyPress(key)
        repaint()
      case _: FocusLost => repaint()
    }

    override def paintComponent(g: Graphics2D) = {
      super.paintComponent(g)
      //      game.entities.foreach(Painter.draw(_)(g))
      g.setColor(new Color(100, 100, 100))
      Painter.draw(player)(g)
      g.drawString("Press arrows to move and mouseclick to fire " +
        (if (hasFocus) " Press 'c' to clear." else ""), 10, size.height - 10)
      g.setColor(Color.black)
    }
  }

  def top = new MainFrame {
    title = "Tank wars"
    contents = mainScreen
  }

}
