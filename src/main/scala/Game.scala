import java.awt.Color

import data.World
import engine.ImageFun.Image
import engine.{Draw, ImageMonad}

import scala.language.higherKinds
import scala.languageFeature.higherKinds
import scala.swing.event.Key
import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.{Free, Monad}

class Game {

  type Input = Key.Value
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
  
  def render[A](world: World): Image[Color] = world match {
    case 2 ⇒ ImageMonad.point(Color.green)
    case 1 ⇒ ImageMonad.point(Color.red)
    case _ ⇒ ImageMonad.point(Color.yellow)
  }

  def start: IO[Unit] = {
    IO.ioMonad.forever(loop(0).point[IO])
  }

  def loop(w: World): IO[World] = for {
    key ← read  
    val world = worldTick(w, key)
    val image = render(world)
    _ ← screen.show(image).point[IO]
    _ ← IO.putStrLn(world.toString)
    //    def step(world: World): IO[World] = for {
    //      key ? ScreenOps.readInput
    //      _ ? ScreenOps.draw(ImageMonad.point(world))
    //    } yield worldTick(world)
    //
    //    whileMprop(step)(start)(endCondition)(IO.ioMonad)
  } yield world

  //  type IOState[+A, B] = StateT[IO, A, B]

  //  type WorldMonad = State[World, Image[Color]]
  //  type WorldMonadT[M[+_]] = StateT[M, World, Image[Color]]
  //  type WorldMonadIO = WorldMonadT[IO]

  def initializeWorld(): World = {
    0
  }

//  def draw(image: Image[Color]): IO[Unit] = {
//    IO.io(rw => Free.return_(rw -> {
//      new Draw(500, 500, image).show() // should not be always new
//      ()
//    }))
//  }

  def whileMprop[M[_], A](f: A ⇒ M[A])(a: A)(p: A ⇒ Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ⇒ whileMprop(f)(n)(p)(M))
}

object ScreenOps {
  def draw[A](image: Image[A]): IO[Unit] = ???

  def readInput: IO[Key.Value] = ???

//  ifReadyDo :: Handle -> IO a -> IO (Maybe a)
//  ifReadyDo hnd x = hReady hnd >>= f
//    where f True = x >>= return . Just
//  f _    = return Nothing

  //  World => (Input, World) -> World => Draw World -> World
}
