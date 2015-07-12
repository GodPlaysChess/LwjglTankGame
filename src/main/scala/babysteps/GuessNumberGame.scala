package babysteps

import com.nicta.rng.Rng

import scala.language.higherKinds
import scalaz.Alpha.A
import scalaz._
import scalaz.effect.IO
import scalaz.effect.IO._

/**
 * 29-May-2015.
 */
object GuessNumberGame {
  def main(args: Array[String]) {
//    GuessNumber.bullsCows.run.flatMap(logandres ⇒ putStrLn(logandres._1.mkString("\n"))).unsafePerformIO()
    GuessNumber.bullsCows.written.flatMap(
      log ⇒ putStrLn(log.mkString("Log:\n", "\n", "\n=========="))
    ).unsafePerformIO()
  }
}

object GuessNumber {

  /* Implicits */
  implicit val listMonoid = scalaz.std.AllInstances.listMonoid[String]
  implicit val stringMonoid = scalaz.std.AllInstances.stringInstance.monoid
  implicit val WMT = WriterT.writerTMonad[IO, List[String]]


  def guessNumber: IO[Unit] = for {
    goal ← requireMaxNumber
    realNumber ← Rng.chooseint(0, goal.toInt).run
    amountOfTries ← ioMonad.whileM(askGuess(realNumber), putStrLn("It is a wrong guess"))
    _ ← putStrLn(s"finally, you guessed from ${amountOfTries.size + 1} try")
  } yield ()

  private def requireMaxNumber: IO[Int] =
    ioMonad.iterateWhile(askAndReadNumber)(_ forall notDigit) map (_.toInt)

  private def notDigit: (Char) ⇒ Boolean =
    !Character.isDigit(_)

  private def askAndReadNumber: IO[String] = for {
      _ ← putStrLn("Enter a number please")
      number ← readLn
    } yield number

  private def askGuess(number: Int): IO[Boolean] = for {
    _ ← putStrLn("Enter your guess")
    guess ← readLn
  } yield guess.toInt != number

  implicit val listMonadPlus: MonadPlus[List] = scalaz.std.AllInstances.listInstance

  /* =============================================== */

  def bullsCows: WriterT[IO, List[String], Unit] = for {
    given ← liftIo1(Rng.chooseint(1000, 1002).run) :++>> (v ⇒ List("Secret number is " + v))
    _ ← WMT.iterateUntil(liftIo(gameLoop(given)))(winningCondition(given, _))
    _ ← liftIoEmptyLog(putStrLn(s"you won, not bad"))
  } yield ()

  private def require4digitInt: IO[Int] = {
    ioMonad.iterateUntil(askAndReadNumber)(s ⇒ (s forall Character.isDigit) && (s.length == 4)) map (_.toInt)
  }

  private def gameLoop(given: Int): IO[Int] = for {
    guess ← require4digitInt
    (bulls, cows) = calculateBullsCows(given, guess)
    _ ← putStrLn(s"$bulls bulls, $cows cows")
  } yield guess

  private def calculateBullsCows(given: Int, guess: Int): (Int, Int) = {
    val bulls = given.toString.zip(guess.toString) count (t ⇒ t._1 == t._2)
    val cows = given.toString.count(char ⇒ guess.toString.contains(char))
    (bulls, cows - bulls)
  }

  private def winningCondition(goal: Int, entered: Int): Boolean =
    goal == entered

  /* u can use it for ugly counting solution.  tries ← whileMprop[IO, (Int, Int)](s ⇒ gameLoop(given, s._2))(0 → 0)(s ⇒ winningCondition(given, s._1))(ioMonad) */
  private def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (p(a)) M.point(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))

  // Almost natural transformation: WriterT[Id, W, A] ~> WriterT[IO, W, A]
  private def liftW[W, A](w: Writer[W, A]): WriterT[IO, W, A] =
    WriterT(IO(w.run))

  private def liftIo[A](w: IO[A]): WriterT[IO, List[String], A] =
    WriterT(w map (a ⇒ (List("tried " + a), a)))

  type WriterTIO[A] = WriterT[IO, List[String], A]
  private def liftIo1[A](w: IO[A]): WriterT[IO, List[String], A] =
    w.liftIO[WriterTIO]

  private def liftIoEmptyLog[A](w: IO[A]): WriterT[IO, List[String], A] =
    liftIoLog(w)(_ ⇒ List.empty)

  private def liftIoLog[A](w: IO[A])(log: A ⇒ List[String]): WriterT[IO, List[String], A] =
    WriterT(w map (a ⇒ (log(a), a)))

  private def liftIoLog1[A](w: IO[A])(log: A ⇒ List[String]): WriterT[IO, List[String], A] =
    WriterT(w map (a ⇒ (log(a), a)))


//  private def liftIo1[A](w: IO[A]): WriterT[IO, List[String], A] =
//    MaybeT.maybeTMonadTrans.liftM(w)(IO.ioMonad)

  //def liftM[G[_] : Monad, A](a: G[A]): F[G, A]



}
