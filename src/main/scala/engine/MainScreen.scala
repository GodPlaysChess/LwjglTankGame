package engine

import java.awt.image.BufferedImage
import java.awt.{Color, Dimension, Graphics2D}

import engine.ImageFun.Image

import scala.swing.{Frame, MainFrame, Panel}

class MainScreen(cols: Int, rows: Int) {

  private val buffer = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB)

  private val mainScreen = new Panel {
    background = Color.white
    preferredSize = new Dimension(cols, rows)
    focusable = true

    override def paint(g: Graphics2D): Unit = {
      g.drawImage(buffer, 0, 0, null)
    }
  }

  private val top: Frame = new MainFrame {
    title = "Tank wars"
    contents = mainScreen
    centerOnScreen()
  }

  def rasterize(image: Image[Color]) =
    for (x <- 0 to cols - 1; y <- 0 to rows - 1)
      buffer.setRGB(x, y, image(x, y).getRGB)

  def repaint() = mainScreen.repaint()

  def open() = top.open()
}
