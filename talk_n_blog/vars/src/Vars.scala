import scala.beans.BeanProperty

/**
  * Author: Krzysztof Romanowski
  */
class PublicVar {
  var publicVar = 123

}

class PrivateVar {
  private var privateVar = 456
}

class JavaBeanVar {
  @BeanProperty
  var javaBeanVar = 789
}

class UsingJavaVar extends JavaVar
