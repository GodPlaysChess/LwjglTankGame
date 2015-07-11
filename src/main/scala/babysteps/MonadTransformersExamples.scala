package babysteps

import babysteps.MTHelper._

import scala.language.postfixOps
import scalaz.Alpha.A
import scalaz.effect.{LiftIO, MonadIO, IO}
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
    _ ← putLn(result)
  } yield()

  def safeIO1(li: List[Maybe[String]]): MaybeT[IO, Int] = for {
    _ ← liftIo(entrance)
    res ← MaybeT(computeInIo(li))
    _ ← liftIo(putStrLn("I am inside a transformer now, check this out").flatMap(_ ⇒ putStrLn((res * 2).toString)))
  } yield res

  def main (args: Array[String]) {
    val strings: List[Maybe[String]] = Just("hello") :: Just("I") :: Just("am") :: Empty[String]() :: Nil
//    safeIO(strings).unsafePerformIO()
    safeIO1(strings).run.unsafePerformIO()
  }

}

object MTHelper {
  type MaybeIOT[A] = MaybeT[IO, A]

  def liftIo[A](m: IO[A]): MaybeT[IO, A] =
    MaybeT(m map (Just(_)))
  
  def liftMb[A](m: Maybe[A]): MaybeT[IO, A] =
    MaybeT(m.pure[IO])

//  def toMb[A](ioa: IO[A]): MaybeT[IO, A] = {
//    implicit val MbIO = MaybeT.maybeTMonadPlus[IO]
//    implicit val sd = optionTLiftIO
//    implicit val Mb = scalaz.Maybe.maybeInstance
//    ioa.liftIO[MaybeIOT]//(MbIO) //[MaybeT[IO, A]](MbIO)
//  }

//  /
    // def liftIO[MaybeT[_]](implicit m: MonadIO[M]): MaybeT[A] =/                /\
    //             __/||\__
   //                 ||
  //                             MonadIO . of Maybe

}
