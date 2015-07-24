package functionalengineattempt

import functionalengineattempt.ImageFun._

import scalaz.effect.IO

object ScreenOps {
  def draw[A](image: Image[A]): IO[Unit] = ???
  def readInput: IO[Input] = ???

  type Input = Int
//  ifReadyDo :: Handle -> IO a -> IO (Maybe a)
//  ifReadyDo hnd x = hReady hnd >>= f
//    where f True = x >>= return . Just
//  f _    = return Nothing

  //  World => (Input, World) -> World => Draw World -> World
}
