package babysteps

import scalaz.effect.IO

trait ActorService {
  def actor(id: String): IO[Option[Actor]]
}
