package babysteps

import scalaz.{State, Monad}
import scalaz.Scalaz._

import scalaz.effect.IO

/**
 * 22-May-2015.
 * Motivation is that Combining alltogether State, Reader and IO is too big step for me.
 * So here I am going to increase complexity step by step, trying to reach at least monad transformers
 */
object IOExcersices {

  def main(args: Array[String]) {
    incrementingWithCondition(1).unsafePerformIO()
  }

  /*
  * main = iterateM_
  *     (\w -> displayWorld w >> return (gameLoop w)) initWorld
  *
  *     Or
  *
  *     main = loop initWorld
  *         where loop w = displayWorld w >> loop (gameLoop w)
  * */


  def incrementInMonadWithState: State[Int, Boolean] = {
    import scalaz._
    def incIOState(input: IO[String]): State[Int, IO[String]] = for {
        i ← get[Int]
        _ ← put[Int](i)
      } yield {
        IO.readLn
      }

    val stateio: State[Int, IO[String]] = incIOState(IO{ "2" })
    val iostate = stateio.sequence

    IO.ioMonad.iterateWhile(incIOState)(s ⇒ s.get[Boolean])
  }


  def incrementingWithCondition(start: Int) = {
    def step(world: Int): IO[Int] = for {
      char ← IO.readLn
      _ ← IO.putLn(char + " counting: " + world + "..")
    } yield world + 1

    whileMprop(step)(start)(_ < 5)(IO.ioMonad)
  }

  def echoWithIncrementRecursive(world: Int): IO[Int] = IO.ioMonad.join(for {
    char ← IO.readLn
    _ ← IO.putLn(char +" counting: " + world + "..")
  } yield echoWithIncrementRecursive(world + 1))


  def incrementInMonad(start: Int) = {
    def echoWithIncrementOne(world: Int): IO[Int] = for {
      char ← IO.readLn
      _ ← IO.putLn(char + " counting: " + world + "..")
    } yield world + 1

    iterateM[IO, Int, Int](echoWithIncrementOne)(start)(IO.ioMonad)
//    IO.ioMonad.iterateUntil(echoWithIncrementOne(start))(_ > 40)
  }

  def iterateM[M[_], A, B](f: A ⇒ M[A])(a: A)(implicit M: Monad[M]): M[B] =
    M.bind(f(a))(n ⇒ iterateM(f)(n)(M))

  def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))

  def keepGoing(i: Int): Boolean = ???

  def eternalConditionalEcho: IO[Unit] = {
    def loop: IO[String] = for {
      key ← IO.readLn
      _ ← IO.put(text(key))
    } yield text(key)
    IO.ioMonad.forever(loop)
  }

  private[this] def text(s: String) = s match {
    case "l" ⇒ "you went left"
    case "r" ⇒ "you went right"
    case "f" ⇒ "do not know word F"
    case _ ⇒ "left or right?"
  }
}
