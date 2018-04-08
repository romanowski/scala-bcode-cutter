libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

	TaskKey[Unit]("bcode"):= {
		val bcode = baseDirectory.value  / s"bcode-${scalaBinaryVersion.value}"
		bcode.mkdirs()

		val output = classDirectory.in(Compile).value.toPath
		compile.in(Compile).value.readStamps().getAllProductStamps.keys.foreach { classFile =>
			val relativePath = output.relativize(classFile.toString)
			val className = relativePath.toString().replace('/', '.')
			s"javap -cp $output -p -c $className" #> bcode./(s"$className.txt") !

			s"javap -cp $output -p -c -s -v -l $className" #> bcode./(s"$className-full.txt") !

			s"javap -cp $output -p -constants $className" #> bcode./(s"$className-sigs.txt") !
		}
	}


enablePlugins(JmhPlugin)