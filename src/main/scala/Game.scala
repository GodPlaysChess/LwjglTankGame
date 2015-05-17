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

  // should it be done inside one big IO ? May be first goal is to draw animation without any input.
  def start() = {
    val world: World = initializeWorld()

    // loop until exit is read.
    //shall be something like: IO.run(world)
    //and it reads Input
    // modifies World state
    // draws (actually combines the previous image with a next image)
    // yields IO[Draw] if state yields true else loops again

    val StateWorld = StateT.stateMonad[World]
    for {
      key <- IO.readLn
      image = f(key)
//        input <- readInput()
//      world = f(input)         l
//      image = f1(world)
      _ <- draw(image)
    } yield ()

    //    loop.unsafePerformIO().show()

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

  def showMainScreen(image: Image[Color]): IO[Input] = IO {
    new MainScreen(640, 480).rasterize(image)
  }

}
