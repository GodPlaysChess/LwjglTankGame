import scalaz.effect.IO

object Main {


  def main(args: Array[String]) {
    safeMain.unsafePerformIO()
  }

  def safeMain: IO[Unit] = {
     new Game().start
  }

//  val readKeyboardIO: IO[Image[Color]] = new Game().start1()
//  val readKeyboardIO: IO[String] = ???
//  val drawScreenIO: IO[Image] = ???
//
//  readKeyboardIO.unsafeZipWith(drawScreenIO, (key, image) ⇒ f(key)).unsafePerformIO()
//
//  def f(s: String): Image[Color] =
//    if (s == "l") ImageMonad.point(Color.gray) else ImageMonad.point(Color.green)

}


