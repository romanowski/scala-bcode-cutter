class NestedFunction {
  def outer(a: Int, unused: String): Int ={
    def inner(b: Int) = a + b
    inner(a)
  }
}

class NestedObject {
  def outer(a: Int): String ={
    object inner
    inner.toString
  }
}
