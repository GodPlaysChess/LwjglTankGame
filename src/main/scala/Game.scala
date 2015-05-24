import java.awt.Color

import data.World
import engine.ImageFun.Image
import engine.{Draw, ImageMonad, MainScreen}

import scala.languageFeature.higherKinds
import scala.swing.event.Key
import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.{Free, Monad}

class Game {

  type Input = Key.Value
  type Output = Boolean
  val screen: MainScreen = new MainScreen(640, 480)

  def endCondition: (World) ? Output = _ ? false

  def worldTick(world: World): World = {
    ???
  }

  def start(start: World) = {
    def step(world: World): IO[World] = for {
      key ? ScreenOps.readInput
      _ ? ScreenOps.draw(ImageMonad.point(world))
    } yield worldTick(world)

    whileMprop(step)(start)(endCondition)(IO.ioMonad)
  }

  //  type IOState[+A, B] = StateT[IO, A, B]

   //  type WorldMonad = State[World, Image[Color]]
  //  type WorldMonadT[M[+_]] = StateT[M, World, Image[Color]]
  //  type WorldMonadIO = WorldMonadT[IO]

  def initializeWorld(): World = {
    World()
  }

  def f(s: String): Image[Color] =
    if (s == "l") ImageMonad.point(Color.gray) else ImageMonad.point(Color.gray)

  def draw(image: Image[Color]): IO[Unit] = {
    IO.io(rw => Free.return_(rw -> {
      new Draw(500, 500, image).show() // should not be always new
      ()
    }))
  }

  def whileMprop[M[_], A](f: A ? M[A])(a: A)(p: A ? Boolean)(implicit M: Monad[M]): M[A] =
    if (!p(a)) f(a) else M.bind(f(a))(n ? whileMprop(f)(n)(p)(M))
}

object ScreenOps {
  def draw[A](image: Image[A]): IO[Unit] = ???

  def readInput: IO[Key.Value] = ???
}
