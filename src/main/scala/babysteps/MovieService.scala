package babysteps

import scalaz.effect.IO

trait MovieService {
  def actorIds(movieId: String): IO[List[String]]
}
