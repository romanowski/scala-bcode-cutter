/**
  * Author: Krzysztof Romanowski
  */
class Default {
  def withDefault(param: String = "baz") = param
  def withoutDefault(param: String) = param
}

class DefaultInAction {
  def a1(d: Default) = d.withDefault()
  def a2(d: Default) = d.withDefault("ala")
  def a3(d: Default) = d.withoutDefault("ala")
}