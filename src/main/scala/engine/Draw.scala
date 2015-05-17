package engine

import engine.ImageFun.Image

import scala.swing.Color

class Draw(cols: Int, rows: Int, image: Image[Color]) {
  def show() = {
    val display = new MainScreen(cols, rows)
    display.rasterize(image)
    display.open()
  }
}