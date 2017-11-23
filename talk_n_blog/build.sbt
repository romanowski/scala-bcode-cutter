def baseSettings = Seq(
  TaskKey[Unit]("bcode"):= {
  val bcode = baseDirectory.value  / s"bcode-${scalaBinaryVersion.value}"
  bcode.mkdirs()

    val output = classDirectory.in(Compile).value
    compile.in(Compile).value.relations.classes._2s.foreach { className =>
      s"javap -cp $output -p -c $className" #> bcode./(s"$className.txt") !

      s"javap -cp $output -p -c -s -v -l $className" #> bcode./(s"$className-full.txt") !

      s"javap -cp $output -p -constants $className" #> bcode./(s"$className-sigs.txt") !
    }
  },
  (scalaSource in Compile) := baseDirectory.value / "src",
  scalaVersion := "2.12.4"
)

def with211 = Seq(
  scalaVersion := "2.11.8"
)

val defaultParameters = project settings(baseSettings)
val vars = project settings(baseSettings)
val lambdas = project settings(baseSettings)
val lazyVals = project settings(baseSettings)
val nested = project settings(baseSettings)
val traits = project settings(baseSettings)
val clazz = project settings(baseSettings)


val nested211 = project settings(baseSettings ++ with211)
val traits211 = project settings(baseSettings ++ with211)
