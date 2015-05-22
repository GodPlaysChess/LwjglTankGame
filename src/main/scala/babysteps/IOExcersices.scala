package babysteps

import scalaz.Scalaz._
import scalaz.State
import scalaz.effect.IO

/**
 * 22-May-2015.
 * Motivation is that Combining alltogether State, Reader and IO is too big step for me.
 * So here I am going to increase complexity step by step, trying to reach at least monad transformers
 */
object IOExcersices {

  def main(args: Array[String]) {
    echoWithIncrement.unsafePerformIO()
  }

  // !!!!! How to get a value out of IO context?
  def echoWithIncrement: IO[State[Int, Unit]] = {
    def loop: IO[State[Int, Unit]] = for {
      key ← IO.readLn
      _ ← IO.put("typed " + i)
    } yield i + 1
    IO.ioMonad.forever(loop(0))
  }

  def eternalConditionalEcho: IO[Unit] = {
    def loop: IO[String] = for {
      key ← IO.readLn
      _ ← IO.put(text(key))
    } yield text(key)
    IO.ioMonad.forever(loop)
  }

  def text(s: String) = s match {
    case "l" ⇒ "you went left"
    case "r" ⇒ "you went right"
    case "f" ⇒ "do not know word F"
    case _ ⇒ "left or right?"
  }


}
