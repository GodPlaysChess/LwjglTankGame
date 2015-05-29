package babysteps

import scala.language.higherKinds
import scalaz.effect.IO
import scalaz.effect.IO._
import scalaz.{Monad, MonadPlus}

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
    amountOfTries ← ioMonad.whileM(askGuess(goal), putStrLn("no"))
    _ ← putStrLn(s"finally, you guessed from ${amountOfTries.size + 1} try")
  } yield ()
  
  def maxNumber: IO[String] = for {
    _ ← putStrLn("Enter max number")
    maxN ← readLn
  } yield maxN
  
  def askGuess(number: String): IO[Boolean] = for {
    _ ← putStrLn("Enter your guess")
    guess ← readLn
  } yield guess != number

  implicit val listMonadPlus: MonadPlus[List] = scalaz.std.AllInstances.listInstance


  def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))

  def whileCounting[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))


}
