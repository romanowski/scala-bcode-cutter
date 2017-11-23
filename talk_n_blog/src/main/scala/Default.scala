package pck

class Default {
  def foo(bar: Int = 123) = bar + 456
}

class DefaultUsed {
  private val default = new Default()

  def withDefault = default.foo()
  def withoutDefault = default.foo(789)
}

class Lambda {
  def play(a: Int => Int) = a(1)

  def test = play(_ + 1)
}

class Basic

class BasicWithType {
  type BasicInt = Int
}


class PrivateThis {
  protected[pck] def foo = 1

  protected def bar = 1

  private def baz = 1
}

