package game.mech

import math._

object Math {
  // Cartesian distance between two points.
  def distance(x1: Double, y1: Double, x2: Double, y2: Double): Double =
    sqrt(pow(y1 - y2, 2) + pow(x1 - x2, 2))

  // Modulus which returns a positive result, even for
  // negative numerators.
  def modDouble(x: Double, y: Double): Double = {
    val m = x % y
    if (m < 0) m + y else m
  }

  // Modulus which returns a positive result, even for
  // negative numerators.
  def modInt(x: Int, y: Int): Int = {
    val m = x % y
    if (m < 0) m + y else m
  }

  // Make sure intensity value is in the range [0, 255] inclusive.
  def clampIntensity(intensity: Int) =
    min(max(intensity, 0), 255)

}
