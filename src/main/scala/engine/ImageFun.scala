package engine

import java.io.File
import javax.imageio.ImageIO

import engine.ImageFun.Image

import scala.swing.Color
import mech.Math._

import scalaz.Monad

object ImageFun {

  type Image[T] = (Double, Double) => T
  type ImageTrans[T] = Image[T] => Image[T]
  type Animation[T] = Double => Image[T]
  type CoordTrans = (Double, Double) => (Double, Double)

  def mapImage[A, B](fun: A => B, image: Image[A]): Image[B] =
    (col, row) => fun(image(col, row))

  def combineImage[A, B, C](image1: Image[A], image2: Image[B], combine: (A, B) => C): Image[C] =
    (col, row) => combine(image1(col, row), image2(col, row))

  def combineMany[T](images: List[Image[T]], combine: (T, T) => T): Image[T] =
    images.reduceLeft((image1, image2) => combineImage(image1, image2, combine))

  /* Want to write:
  def coordTrans[T](trans:CoordTrans):ImageTrans[T] =
     (image:Image[T]) => image.tupled compose trans
  */

  // Transform the coordinates of an image by some function
  def coordTrans[T](trans: CoordTrans): ImageTrans[T] =
    (image: Image[T]) =>
      (col: Double, row: Double) =>
        image.tupled(trans(col, row))

  // Apply an image transformation about a point
  def aboutPoint[T](transform: ImageTrans[T], col: Double, row: Double): ImageTrans[T] =
    translate(-col, -row) andThen transform andThen translate(col, row)

  // Translate an image
  def translate[T](colDelta: Double, rowDelta: Double): ImageTrans[T] =
    coordTrans((col, row) => (col - colDelta, row - rowDelta))

  // Scale image about the origin
  def scaleOrigin[T](factor: Double): ImageTrans[T] =
    coordTrans((col, row) => (col / factor, row / factor))

  // Scale image about a center point
  def scale[T](factor: Double,
               centerCol: Double, centerRow: Double): ImageTrans[T] =
    aboutPoint(scaleOrigin(factor), centerCol, centerRow)

  // Rotate image clockwise about the origin, angle in radians
  def rotateOrigin[T](angle: Double): ImageTrans[T] = {
    val cosAngle = math.cos(angle)
    val sinAngle = math.sin(angle)
    coordTrans(
      (col, row) =>
        (col * cosAngle - row * sinAngle, col * sinAngle + row * cosAngle))
  }

  // Rotate image clockwise about some point, angle in radians
  def rotate[T](angle: Double,
                centerCol: Double, centerRow: Double): ImageTrans[T] =
    aboutPoint(rotateOrigin(angle), centerCol, centerRow)

  // Return an image from a bitmap file, given a filepath to the image.
  // Bitmap is tiled infinitely in all directions.
  def bitmap(filepath: String): Image[Color] = {
    val pixels = ImageIO.read(new File(filepath))
    val numRows = pixels.getHeight
    val numCols = pixels.getWidth
    (col: Double, row: Double) => {
      val colInt = modInt(col.toInt, numCols)
      val rowInt = modInt(row.toInt, numRows)
      new Color(pixels.getRGB(colInt, rowInt))
    }
  }
}

// looks really like Reader
object ImageMonad extends Monad[Image] {
  override def point[A](a: => A): Image[A] = (_, _) => a

  override def bind[A, B](fa: Image[A])(f: (A) => Image[B]): Image[B] =
    (col, row) => f(fa(col, row))(col, row)

}
