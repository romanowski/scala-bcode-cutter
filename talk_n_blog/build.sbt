TaskKey[Unit]("bcode") := {
  val bcode = file("bcode")
  bcode.mkdirs()


  val output = classDirectory.in(Compile).value
  compile.in(Compile).value.relations.classes._2s.foreach { className =>
    s"javap -cp $output -p -c $className" #> bcode./(s"$className.txt") !

    s"javap -cp $output -p -c -s -v -l $className" #> bcode./(s"$className-full.txt") !

    s"javap -cp $output -p -constants $className" #> bcode./(s"$className-sigs.txt") !
  }
}