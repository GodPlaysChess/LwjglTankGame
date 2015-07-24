package game.data

import game.entities.Drawable

/**
 * Contains all game game.data
 */
case class World(background: Vector[Drawable] = Vector(),
                 terrain: Vector[Drawable] = Vector(),
                 players: Vector[Drawable] = Vector(),
                 projectiles: Vector[Drawable] = Vector(),
                 foreground: Vector[Drawable] = Vector()) {

}
