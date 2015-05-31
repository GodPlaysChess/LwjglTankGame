package babysteps

import com.nicta.rng.Rng

import scala.language.higherKinds
import scalaz.Alpha.M
import scalaz.effect.{IORef, IO}
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
    goal ← requireMaxNumber
    realNumber ← Rng.chooseint(0, goal.toInt).run
    amountOfTries ← ioMonad.whileM(askGuess(realNumber), putStrLn("no"))
    _ ← putStrLn(s"finally, you guessed from ${amountOfTries.size + 1} try")
  } yield ()
  
  def requireMaxNumber: IO[Int] =
    ioMonad.iterateWhile(askAndReadNumber)(_ forall notDigit) map (_.toInt)


  private def notDigit: (Char) ⇒ Boolean =
    !Character.isDigit(_)

  def askAndReadNumber: IO[String] =
    for {
    _ ← putStrLn("Enter a number please")
    maxN ← readLn
  } yield maxN

  def askGuess(number: Int): IO[Boolean] = for {
    _ ← putStrLn("Enter your guess")
    guess ← readLn
  } yield guess.toInt != number

  implicit val listMonadPlus: MonadPlus[List] = scalaz.std.AllInstances.listInstance


  def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))

  def whileCounting[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))


}
