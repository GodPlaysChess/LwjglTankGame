import scalaz._
import Scalaz._
import scalaz.effect.IO
import scala.language.higherKinds

object StateMonadTransformerExample {
  def getActors(movieIds: List[String],
                movieService: MovieService,
                actorService: ActorService): IO[Map[String, List[Actor]]] = {
    val movieIdsForActors = movieIds traverse { movieId =>
      for {
        actorIds <- movieService.actorIds(movieId)
        actors <- actorIds traverse { actorId =>  // List[String] actors => List[ IO[Option[Actor]] => IO[List[Option[Actor]]
          actorService.actor(actorId)
        }
      } yield (movieId, actors.flatten)     // IO[  String, List[String]  ]
    }                                    // List [ IO [String, List[String]  ] => IO [List[(String, List[String])]]
    movieIdsForActors map { _.toMap }
  }

}

case class Actor(id: String, name: String)

trait MovieService {
  def actorIds(movieId: String): IO[List[String]]
}

trait ActorService {
  def actor(id: String): IO[Option[Actor]]
}


