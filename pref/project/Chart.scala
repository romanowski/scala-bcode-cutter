import java.nio.file.Path

import sbt.Keys._
import sbt._
import spray.json.DefaultJsonProtocol._
import spray.json._

object Charts {
  val warmupChart = TaskKey[Path]("warmupChart")
  val runBenchmark = TaskKey[Path]("Run bechmark")

  case class Percentiles(`50.0`: Double, `90.0`: Double, `95.0`: Double, `99.0`: Double)

  case class MetricData(score: Double, rawData: Option[Array[Array[Double]]], scorePercentiles: Percentiles)

  case class Benchmark(benchmark: String, primaryMetric: MetricData)

  private implicit val percentilesFormat = jsonFormat4(Percentiles)
  private implicit val mdFormat = jsonFormat3(MetricData)
  private implicit val benchFormat = jsonFormat2(Benchmark)


  private def drawWarmingCharts(data: Array[Benchmark], base: File): Unit = {
    val batch = 10
    val initial = 300
    val margin = 4


    (chartBase / "warming-full").mkdir()
    (chartBase / "warming-partial").mkdir()

    data.zipWithIndex.foreach { case (metric, index) =>
      val rawTimes = metric.primaryMetric.rawData.get.head
      val avg = metric.primaryMetric.scorePercentiles.`50.0`
      val strAvg = "%.4f".format(avg)

      def draw(rawData: Array[Double]): Unit = {
        val name = metric.benchmark.split('.').last
        val csv = rawData.zipWithIndex.map { case (t, i) => s"$i\t$t" }.mkString("\n")
        val dataFile = base / s"data-$name.csv"
        IO.write(dataFile, csv)
        val file = (base / s"plot-$name.sh").getAbsoluteFile
        val fullChart = rawData == rawTimes
        val command =
          s"""set term png size 1920, 1080
             |set output '$chartBase/warming-${if(fullChart) "full" else "partial"}/${name.replace(" ", "-").toLowerCase}.png'
             |set title '$name'
             |${if(fullChart) "" else "set logscale y 2"}
             |set yrange [1:${if (fullChart) 28 else 256}]
             |# set arrow from $initial, graph 0 to $initial, graph 4 nohead
             |plot '$dataFile' with lines lt $index title 'times', $avg title 'p0.50 = $strAvg' lt ${index + 1} lw 3
             |""".stripMargin

        IO.write(file, command)
        import scala.sys.process._
        s"gnuplot $file".!
      }

      draw(rawTimes)
      draw(rawTimes.take(500))
    }
  }

  def chartBase = file("charts").getAbsoluteFile

  private def summary(data: Array[Benchmark], base: File, chartName: String): Unit = {
    val filename = chartName.replace(" ", "-")
    val csv = data.sortBy(_.primaryMetric.scorePercentiles.`50.0`).map { b =>
      val p = b.primaryMetric.scorePercentiles
      Seq(b.benchmark.split('.').last, p.`99.0`, p.`95.0`, p.`90.0`, p.`50.0`).mkString("\t")
    }.mkString("\n")
    val dataFile = base / s"$filename.csv"
    IO.write(dataFile, csv)

    def plotSet(name: String, column: Int) =
      s"'$dataFile' using ${column + 2}:xtic(1) title '$name'"

    val plotCmd = Seq("99.0", "95.0", "90.0", "50.0").zipWithIndex.map((plotSet _).tupled)

    val command =
      s"""
         |set term png size 1920, 1080
         |set output '$chartBase/$filename.png'
         |set key left
         |set grid y
         |set xtics rotate
         |set style data histograms
         |#set style histogram rowstacked
         |set boxwidth 0.80
         |set style fill solid 1.0 border -1
         |set title '$chartName'
         |plot ${plotCmd.mkString(", ")}
      """.stripMargin

    val scriptFile = (base / s"plot-$filename.sh").getAbsoluteFile
    IO.write(scriptFile, command)
    import scala.sys.process._
    s"gnuplot $scriptFile".!
  }

  def implementWarmupChart = warmupChart := {
    val base = streams.value.cacheDirectory
    val chart = base.toPath.resolve("data.csv")
    val json = IO.read(runBenchmark.value.toFile).parseJson
    val data = json.convertTo[Array[Benchmark]]
    streams.value.log.success(s"Saving to $base using: ${data.toSeq.mkString("\n")}")
    val (warming, hot) = data.partition(_.primaryMetric.rawData.isDefined)

    chartBase.mkdir()
    drawWarmingCharts(warming, base)
    summary(hot, base, "Hot benchmarks")
    summary(warming, base, "Warming benchmarks")
    base.toPath
  }


  def settings = Seq(
    runBenchmark := {
      val f = file("all.json").toPath.toAbsolutePath
      //run.in(JmhKeys.Jmh).toTask(s" WarmingBenchmark.* -rff ${file("out.json").toPath.toAbsolutePath} -rf json -v silent").value
      f
    },
    implementWarmupChart
  )

}
