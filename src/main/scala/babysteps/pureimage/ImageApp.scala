//package babysteps.pureimage
//
//import ps.tricerato.pureimage
//import ps.tricerato.pureimage._
//
///**
// * 10-Jul-2015.
// */
//object ImageApp {
//
//  Input(resource("/IMG_0501.jpg")) match {
//    case Right(RGBImage(image)) => image
//    case _ => ???
//  }
//
//  def fib1[A : Pixel](image: Image[A]):Image[A] = {
//    import pureimage.filter._
//    val square = squareCrop(image)
//    new Image[A] {
//      def width = (square.width * PHI).toInt; def height = square.height
//      def apply(x: Int, y: Int) = if (x >= square.width) {
//        image.ops.zero
//      } else {
//        image(x,y)
//      }
//    }
//  }
//
//  def fib2[A : Pixel](image: Image[A]):Image[A] = {
//    import pureimage.filter._
//    val square = squareCrop(image)
//    new Image[A] {
//      def width = (square.width * PHI).toInt; def height = square.height
//      def apply(x: Int, y: Int) = {
//        val flipped = Rotate(Rotate.Clockwise90, image)
//        if (x >= square.width) {
//          flipped(x - square.width, y)
//        } else {
//          image(x,y)
//        }
//      }
//    }
//  }
//
//  def fib3[A : Pixel](image: Image[A]):Image[A] = {
//    import pureimage.filter._
//    val square = squareCrop(image)
//    new Image[A] {
//      def width = (square.width * PHI).toInt; def height = square.height
//      def apply(x: Int, y: Int) = {
//        lazy val flipped = Rotate(Rotate.Clockwise90, fib3(image))
//        if (x >= square.width) {
//          flipped(x - square.width, y)
//        } else {
//          image(x,y)
//        }
//      }
//    }
//  }
//
//}
