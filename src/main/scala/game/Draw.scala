package game

import functionalengineattempt.ImageFun.Image

import scala.swing.Color

class Draw(cols: Int, rows: Int) {
  val display = new MainScreen(cols, rows)

  def show(image: Image[Color]) = {
    display.rasterize(image)
    display.open()
  }
}
