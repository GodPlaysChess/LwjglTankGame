package mech

case class Vec(x: Double, y: Double) {
  def +(other: Vec) = Vec(x + other.x, y + other.y)

  def -(other: Vec) = Vec(x - other.x, y - other.y)

  def *(n: Double): Vec = Vec(n * x, n * y)

  def /(n: Double): Vec = Vec(x / n, y / n)

  def %(n: Int): Vec = Vec(x % n, y % n)

  def %(other: Vec): Vec = Vec(x % other.x, y % other.y)

  def length: Double = math.hypot(x, y)

  def normal: Vec = Vec(x / length, y / length)

  def distance(other: Vec): Double = math.hypot(x - other.x, y - other.y)

  def >(other: Vec): Boolean = x > other.x && y > other.y

  def <(other: Vec): Boolean = x < other.x && y < other.y

  def abs: Vec = Vec(math.abs(x), math.abs(y))
}
