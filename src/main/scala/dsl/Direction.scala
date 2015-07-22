package dsl

/**
 * 22-Jul-2015.
 */
sealed abstract class Direction

case class Left extends Direction
case class Right extends Direction

