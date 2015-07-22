package dsl

import scala.language.higherKinds
import scalaz.{Free, Functor}

/**
 * 22-Jul-2015.
 */
sealed trait Command[+Next]

object Command {

  case class Go[Next](x: Direction, next: Next) extends Command[Next]

  case class Fire[Next](next: Next) extends Command[Next]

  case class Done extends Command[Nothing]


  implicit val commandFunctor: Functor[Command] = new Functor[Command] {
    override def map[A, B](fa: Command[A])(f: (A) ⇒ B): Command[B] = fa match {
      case Go(x, next) => Go(x, f(next))
      case Fire(next) => Fire(f(next))
      case Done() => Done()
    }
  }

  def go(dir: Direction): Free[Command, Unit] =
    Free.Suspend(Go(dir, Free.Return[Command, Unit](())))

  def fire: Free[Command, Unit] =
    Free.Suspend(Fire(Free.Return[Command, Unit](())))

  def done: Free[Command, Unit] =
    Free.Suspend[Command, Unit](Done())

  private def liftF[F[+_]: Functor, R](command: F[R]): Free[F, R] = {
    Free.Suspend[F, R](Functor[F].map(command) { Free.Return[F, R] })
  }

}
