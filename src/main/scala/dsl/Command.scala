package dsl

import dsl.Command.{Done, Fire, Go}

import scala.language.higherKinds
import scalaz.effect.IO
import scalaz.{Free, Functor}

/**
 * 22-Jul-2015.
 */
sealed trait Command[+Next]

object Command {

  case class Go[Next](x: Direction, next: Next) extends Command[Next]

  case class Fire[Next](next: Next) extends Command[Next]

  case class Done() extends Command[Nothing]

  implicit val commandFunctor: Functor[Command] = new Functor[Command] {
    override def map[A, B](fa: Command[A])(f: (A) ⇒ B): Command[B] = fa match {
      case Go(x, next) => Go(x, f(next))
      case Fire(next) => Fire(f(next))
      case Done() => Done()
    }
  }

  implicit val commandMonad = Free.freeMonad[Command]


  def go(dir: Direction): Free[Command, Unit] =
    Free.liftF[Command, Unit](Go(dir, ()))

  def fire: Free[Command, Unit] =
    Free.liftF[Command, Unit](Fire(()))

  def done: Free[Command, Unit] =
    Free.liftF[Command, Unit](Done())

  def pointed[A](a: A) = Free.return_(a)
}

object App {

  import Command._

  def main(args: Array[String]) {
    val program: Free[Command, Unit] = for {
      _ ← go(Left())
      _ ← fire
      _ ← done
    } yield ()

    LogInterpreter.interpret(program)

    val safeIO = for {
      _ ← IO.putStrLn("Interpreting in IO")
      str ← WriterLogInterpreter.interpret(program)
      _ ← IO.putStr(str)
    } yield ()

    safeIO.unsafePerformIO()
  }
}

trait Interpreter[A] {
  def interpret(program: Free[Command, Unit]): A
}

object LogInterpreter extends Interpreter[Unit] {
  override def interpret(program: Free[Command, Unit]): Unit = program.fold({
    _ ⇒ println("out")
  }, {
    case Go(d, next) ⇒
      d match {
        case Left() => println("Going left")
        case Right() => println("Going right")
      }
      interpret(next)
    case Fire(next) ⇒
      println("Fire")
      interpret(next)
    case Done() ⇒ println("======")
  })
}

object WriterLogInterpreter extends Interpreter[IO[String]] {

  import scalaz.syntax.monad._

  val IOM = IO.ioMonad

  override def interpret(program: Free[Command, Unit]): IO[String] = program.fold({
    _ ⇒ IOM.pure("")
  }, {
    case Go(d, next) ⇒ d match {
      case Left() => IOM.map(interpret(next))("Going left\n" + _)
      case Right() => IOM.map(interpret(next))("Going right\n" + _)
    }
    case Fire(next) ⇒ IOM.map(interpret(next))("Fire \n" + _)
    case Done() ⇒ "======".pure[IO]
  })
}

object ActionInterpreter extends Interpreter[Int] {
  override def interpret(program: Free[Command, Unit]): Int = ???
}
