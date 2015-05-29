package babysteps

import scala.language.higherKinds
import scalaz.effect.IO
import scalaz.effect.IO._

/**
 * 29-May-2015.
 */
object GuessNumberGame {
  def main(args: Array[String]) {
    GuessNumber.game.unsafePerformIO()
  }
}

object GuessNumber {

  def game: IO[Unit] = for {
    goal ← maxNumber
    _ ← ioMonad.whileM_(askGuess(goal), putStrLn("no"))
    _ ← putStrLn("finally, you guessed it")
  } yield ()
  
  def maxNumber: IO[String] = for {
    _ ← putStrLn("Enter max number")
    maxN ← readLn
  } yield maxN
  
  def askGuess(number: String): IO[Boolean] = for {
    _ ← putStrLn("Enter your guess")
    guess ← readLn
  } yield guess != number

}
