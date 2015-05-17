import java.awt.Color

import data.World
import engine.ImageFun.Image
import engine.{ImageMonad, Draw, MainScreen}

import scala.swing.event.Key
import scalaz.Scalaz._
import scalaz.{Free, State, StateT}
import scalaz.effect.IO

class Game {
  val screen: MainScreen = new MainScreen(640, 480)

  /* just playing with IO */
  def loopReadFromConsole = for {
    line <- IO.readLn
    _ <- IO.putLn(line)
  } yield ()

  def showEmptyScreen: Draw =
    new Draw(500, 500, ImageMonad.point(Color.red))

  /* end of playing with IO */

  def initializeWorld(): World = {
    World()
  }

  def f(s: String): Image[Color] =
    if (s == "l") ImageMonad.point(Color.gray) else greenImage()

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

  type Input = Key.Value

  //
  //  def loop: IO[Draw] = {
  //    for {
  //      input <- readInput() // IO action
  //      out <- step(input)
  //      world1 <- get[World]
  //      image <- draw(world1) //combine instead of draw?
  //    } yield {
  //      if (out) loop
  //      else image
  //    }
  //  }

  type Output = Boolean

  // whether game is finished or not (may be screen is closed or whatever)
  def step(input: Input): State[World, Output] = ???

  def readInput(): IO[Input] = {
    IO[Input](Key.Left)
  }

  def draw(image: Image[Color]): IO[Unit] = {
    IO.io(rw => Free.return_(rw -> {
      new Draw(500, 500, image).show()
      ()
    }))
  }

  def greenImage(): Image[Color] =
    ImageMonad.point(Color.green)

  // thats the idea, to get Input from the main frame. Still have no clue how to manage it.
  //  def showMainScreen(image: Image[Color]): IO[Input] = IO {
  //    new MainScreen(640, 480).rasterize(image)
  //  }

}
