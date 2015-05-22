import java.awt.Color

import data.World
import engine.ImageFun.Image
import engine.{Draw, ImageMonad, MainScreen}

import scala.languageFeature.higherKinds
import scala.swing.event.Key
import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.{Free, State, StateT}

class Game {

  type Input = Key.Value
  type Output = Boolean
  val screen: MainScreen = new MainScreen(640, 480)

//  type IOState[+A, B] = StateT[IO, A, B]

  def start() = {
    val world: World = initializeWorld()

    //      input <- readInput() | IO[Key.Value]
    //      world = f(input)     | here comes the state, but so far can be still f: World, Key => World    l
    //      image = f1(world)    | graphical representation of that world: World => Image[Color]
    //      _ <- draw(image)     | Image => IO[Unit]
    val StateWorld = StateT.stateMonad[World]
    def loop = {
      for {
        key <- IO.readLn
        image = f(key)
        _ <- draw(image)
      } yield ()
    }
    IO.ioMonad.untilM_(loop, IO{false} )
    //    StateWorld.untilM_(get, loop).run(world)
  }

  def start1: IO[Image[swing.Color]] = {
    def loop: IO[Image[Color]] = {
      for {
        key <- IO.readLn
        image = f(key)
      } yield image
    }
    IO.ioMonad.forever(loop);//untilM(loop, IO{false})(ImageMonad)
  }

//  type WorldMonad = State[World, Image[Color]]
//  type WorldMonadT[M[+_]] = StateT[M, World, Image[Color]]
//  type WorldMonadIO = WorldMonadT[IO]

  def initializeWorld(): World = {
    World()
  }

  def f(s: String): Image[Color] =
    if (s == "l") ImageMonad.point(Color.gray) else greenImage()

  def greenImage(): Image[Color] =
    ImageMonad.point(Color.green)

  def draw(image: Image[Color]): IO[Unit] = {
    IO.io(rw => Free.return_(rw -> {
      new Draw(500, 500, image).show() // should not be always new
      ()
    }))
  }

//  def update(draw: Draw, image: Image[Color]): Draw
//    IO.io(rw => Free.return_(rw -> {
//      new Draw(500, 500, image).show() // should not be always new
//      ()
//    }))

  // whether game is finished or not (may be screen is closed or whatever)
  def step(input: Input): State[World, Output] = ???

  def readInput(): IO[Input] = {
    IO[Input](Key.Left)
  }
}
