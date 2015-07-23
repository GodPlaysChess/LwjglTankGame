package babysteps

import scalaz.concurrent.Future
import scalaz._
import Scalaz._
/**
 * 23-Jul-2015.
 */
object SOverflowQuestion {

  type Z = List[String]
  type F[α] = Future[α]
  type WT[α] = WriterT[F, Z, α]

  implicit val z: Monoid[Z] = ???
  implicit val f: Monad[F] = ???

  def fooA (): WT[Int] = WriterT.put[F, Z, Int] (f.point (18))(z.zero)
  def fooB (): WT[Int] = WriterT.put[F, Z, Int] (f.point (42))(z.zero)
  def fooLog (msg: String): WT[Unit] = WriterT.put[F, Z, Unit] (f.point (()))(msg :: Nil)


  type WTT[α] = WriterT[Future, Z, Throwable \/ α]

  def barA(): WTT[Int] = WriterT.put[F, Z, Throwable \/ Int] (f.point(18.right))(z.zero)
  def barB(): WTT[Int] = WriterT.put[F, Z, Throwable \/ Int] (f.point(42.right))(z.zero)
  def barLog (msg: String): WTT[Unit] = WriterT.put[F, Z, Throwable \/ Unit] (f.point (().right))(msg :: Nil)

  type MLT[α] = EitherT[F, Throwable, α]
  type WLET[α] = WriterT[MLT, Z, α]

  def bazA(): WLET[Int] = WriterT.put[MLT, Z, Int](EitherT.right[F, Throwable, Int](f.point(18)))(z.zero)


  def bar (): WLET[Int] = for {
    a <- bazA()
    b <- bazA()
  } yield a + b

  def liftW[A](fa: Future[A]): WLET[A] = {
    WriterT.put[MLT, Z, A](EitherT.right[Future, Throwable, A](fa))(z.zero)
  }

  def bbar(): WLET[Int] = for {
    a ← liftW(6.point[F])
    b ← liftW(6.point[F])
  } yield a + b

}
