package babysteps

import scalaz._
import scala.language.higherKinds
import scalaz.{IndexedStateT, StateT, Monad}
import scalaz.Scalaz._
import scalaz.effect.IO

/**
 * 22-May-2015.
 * Motivation is that Combining alltogether State, Reader and IO is too big step for me.
 * So here I am going to increase complexity step by step, trying to reach at least monad transformers
 */
object IOExcersices {

  def main(args: Array[String]) {
//    loopingState
    println(simpleLoopUntil(_ > 5)(i ⇒ i + 1)(3))
    //    val (s, i): (String, Int) = ioWIthStateExample("long message").unsafePerformIO()
    //    println("result was %d, final state was %s".format(i, s))
    //    incrementingWithCondition(1).unsafePerformIO()
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

  type StringIO[A] = StateT[IO, String, A]

  def ioWIthStateExample: IndexedStateT[IO, String, String, Int] =
    for {
      _ ← IO.putStrLn("Enter something ").liftIO[StringIO]
      s ← get[String].lift[IO]
      input ← IO.readLn.liftIO[StringIO]
      _ ← put("entered " + input).lift[IO]
    } yield input.length


/*  def loopingState: State[Int, Boolean] = {
    def incr: State[Int, Boolean] = for {
      mod ← modify[Int](_ + 1)
    } yield (mod > 5)

    val StateX = StateT.stateMonad[Int]
    StateX.iterateUntil(incr)(_)
    //    val M = StateT.stateMonad[Int]

    //    M.iterateUntil(incr)(get)
  }*/

  def simpleLoopUntil(p: Int => Boolean)(f: Int ⇒ Int): Int ⇒ Int =
    a ⇒ if (p(a)) a else simpleLoopUntil(p)(f)(f(a))


  //  def incrementInMonadWithState: State[Int, Boolean] = {
  //    import scalaz._
  //    def incIOState(input: IO[String]): State[Int, IO[String]] = for {
  //        i ← get[Int]
  //        _ ← put[Int](i)
  //      } yield {
  //        IO.readLn
  //      }
  //
  //    val stateio: State[Int, IO[String]] = incIOState(IO{ "2" })
  //    val iostate = stateio.sequence
  //
  //    IO.ioMonad.iterateWhile(incIOState)(s ⇒ s.get[Boolean])
  //  }


  def incrementingWithCondition(start: Int) = {
    def step(world: Int): IO[Int] = for {
      char ← IO.readLn
      _ ← IO.putLn(char + " counting: " + world + "..")
    } yield world + 1

    whileMprop(step)(start)(_ < 5)(IO.ioMonad)
  }

  def echoWithIncrementRecursive(world: Int): IO[Int] = IO.ioMonad.join(for {
    char ← IO.readLn
    _ ← IO.putLn(char + " counting: " + world + "..")
  } yield echoWithIncrementRecursive(world + 1))


  def incrementInMonad(start: Int) = {
    def echoWithIncrementOne(world: Int): IO[Int] = for {
      char ← IO.readLn
      _ ← IO.putLn(char + " counting: " + world + "..")
    } yield world + 1

    iterateM[IO, Int, Int](echoWithIncrementOne)(start)(IO.ioMonad)
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
