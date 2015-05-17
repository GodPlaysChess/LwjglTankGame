package data

import entities.Drawable

/**
 * Contains all game data
 */
case class World(background: Vector[Drawable] = Vector(),
                 terrain: Vector[Drawable] = Vector(),
                 players: Vector[Drawable] = Vector(),
                 projectiles: Vector[Drawable] = Vector(),
                 foreground: Vector[Drawable] = Vector()) {

}
