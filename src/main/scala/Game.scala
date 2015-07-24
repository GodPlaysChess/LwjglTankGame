import java.awt.Color

import engine.ImageFun.Image
import engine.{Draw, ImageMonad}

import scala.language.higherKinds
import scala.languageFeature.higherKinds
import scala.swing.event.Key._
import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.{State, Monad}

class Game {

  type Output = Boolean
  type World = Int // for now just int
  val screen = new Draw(640, 480)

  def endCondition: (World) ⇒ Output = _ ⇒ false

  def worldTick(world: World, i: Int): World = {
    (world + i) % 3
  }

  def read: IO[Int] = {
    1.point[IO]
//    System.in.read().point[IO]
  }

  def step(input: Value, time: Long): State[World, Boolean] = for {
    w0 ← get[World]
    val w1 = gameLogic(input, w0)
    val x = Thread.sleep(time)
    _ ← put[World](w1)
  } yield false

  def gameLogic(input: Value, world: World): World = input match {
    case Left ⇒ world + 1
    case Right ⇒ world + 2
    case _ ⇒ 0
  }
  
  def render[A](world: World): Image[Color] = world match {
    case 2 ⇒ ImageMonad.point(Color.green)
    case 1 ⇒ ImageMonad.point(Color.red)
    case _ ⇒ ImageMonad.point(Color.yellow)
  }

  def start: IO[Unit] = {
    whileMprop(loop)(0)(_ < 10)(IO.ioMonad) map (_ ⇒ ())
  }

  def loop(w: World): IO[World] = for {
    key ← read
    val w1 = worldTick(w, key)
    _ ← screen.show(render(w1)).point[IO]
    _ ← IO.putStrLn(w.toString)
    //    def step(world: World): IO[World] = for {
    //      key ? ScreenOps.readInput
    //      _ ? ScreenOps.draw(ImageMonad.point(world))
    //    } yield worldTick(world)
    //
    //    whileMprop(step)(start)(endCondition)(IO.ioMonad)
  } yield w1

  //  type IOState[+A, B] = StateT[IO, A, B]

  //  type WorldMonad = State[World, Image[Color]]
  //  type WorldMonadT[M[+_]] = StateT[M, World, Image[Color]]
  //  type WorldMonadIO = WorldMonadT[IO]

  def initializeWorld(): World = {
    0
  }
  def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))
}

object ScreenOps {
  def draw[A](image: Image[A]): IO[Unit] = ???

  def readInput: IO[Value] = ???

//  ifReadyDo :: Handle -> IO a -> IO (Maybe a)
//  ifReadyDo hnd x = hReady hnd >>= f
//    where f True = x >>= return . Just
//  f _    = return Nothing

  //  World => (Input, World) -> World => Draw World -> World
}
