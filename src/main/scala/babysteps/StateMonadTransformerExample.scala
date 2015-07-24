package babysteps

import scalaz.effect.IO
import scalaz._
import Scalaz._

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
