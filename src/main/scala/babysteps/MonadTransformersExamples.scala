package babysteps

import scala.language.postfixOps
import scalaz.effect.IO
import scalaz.{MaybeT, Monoid, Scalaz, Maybe}
import scalaz.Maybe.{Empty, Just}
import scalaz.effect.IO._

import Scalaz._

/**
 * 10-Jul-2015.
 */
object MonadTransformersExamples {
  /* implicits */
  implicit val ListMonad = scalaz.std.AllInstances.listInstance
  implicit val MbMonad = scalaz.Maybe.maybeInstance
  implicit val Sum = Monoid.instance[Int](_ + _, 0)

  //// Auxiliary instances
  val MbIO = MaybeT.maybeT[IO]

  ////

  def toLength(li: List[Maybe[String]]): Maybe[Int] = {
    val f: (String ⇒ Int) = _.length
    //1
    for {
      seq <- ListMonad.sequence(li)
    } yield ListMonad.foldMap(seq)(f)

    ListMonad.sequence(li) map (ListMonad.foldMap(_)(f))
  }

  def entrance: IO[Unit] =
    putStrLn("hello")

  def computeInIo(li: List[Maybe[String]]): IO[Maybe[Int]] = {
    toLength(li).pure[IO]
  }
                                                               //def liftIO[A](ioa: IO[A]): F[A] || IO -> MayBe
  def safeIO(li: List[Maybe[String]]): IO[Unit] = for {
    _ <- entrance
    result ← computeInIo(li)                                                  //that's the work for monad Transformer
    res ← result
    _ ← putStrLn(res.toString)
  } yield()

  def safeIO1(li: List[Maybe[String]]): MaybeT[IO, Unit] = ??? /* for {
//    _ ← entrance.liftM(MaybeT.maybeTMonadTrans)
  } yield ()                                                  */

  def main (args: Array[String]) {
    val strings: List[Maybe[String]] = Just("hello") :: Just("I") :: Just("am") :: Nil
    safeIO(strings).unsafePerformIO()
    safeIO1(strings).run.unsafePerformIO()
  }

  def main1 (args: Array[String]) {
    val strings: List[Maybe[String]] = Just("hello") :: Just("I") :: Just("am") :: Nil
    toLength(strings) println
  }

}
