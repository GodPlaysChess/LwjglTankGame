package functionalengineattempt

import java.awt.{Color, Dimension, Graphics2D}

import game.data.World
import game.entities.Square
import game.graphics.Painter
import game.mech.{PlayerControl, Vec}

import scala.collection.immutable.Queue
import scala.swing.event.{Key, FocusLost, KeyPressed, MouseClicked}
import scala.swing.{MainFrame, Panel, SimpleSwingApplication}
import scalaz.effect.IO

object CloneOFSwingBasedGame extends SimpleSwingApplication {
  implicit val IM = IO.ioMonad

  override def main(args: Array[String]) {
    safeMain(args).unsafePerformIO()
  }

  def safeMain(args: Array[String]): IO[Unit] = for {
    _ ← IM.point(startup(args))
  } yield ()

  type World = Int

  val player = Square(Vec(100, 200), 30)
  val entities = World(players = Vector(player))
  val control = new PlayerControl(player)
  var actionsQueue = Queue[Action]() // or Free?

  //  def onKeyPress(keyCode: Value): State[World, Unit] = keyCode match {
  //    case Left => State.gets(_ + 1)
  //    case Right => State.gets(_ + 2)
  //    case Up => State.gets(_ + 3)
  //    case Down => State.gets(_ + 4)
  //    case Space => State.gets(_ - 1)
  //    case _ => State.put(0)
  //  }

  def onKeyPress(keyCode: Key.Value): Queue[Action] = {
    Keypress(keyCode) +: actionsQueue
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
        actionsQueue = onKeyPress(key)
//        repaint()
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
