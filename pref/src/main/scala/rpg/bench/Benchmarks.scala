package rpg.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode.SampleTime
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import rpg.PlayerTraits
import rpg.SkillTreeRepr
import rpg.cake2.Cake2SkillTrees
import rpg.patmat.BasicSkillTrees
import rpg.cake.CakeSkillTrees
import rpg.ooo.OOOSkillTrees
import rpg.typeclass.naive.TypeclassSkillTrees


@State(Scope.Benchmark)
class BenchmarkState {
  val playerTraits = new PlayerTraits(100, 100, 100)

  val basic: SkillTreeRepr[_] = (new BasicSkillTrees).archeryTree(playerTraits)
  val typeclass: SkillTreeRepr[_] = (new TypeclassSkillTrees).archeryTree(playerTraits)
  val cake: SkillTreeRepr[_] = (new CakeSkillTrees).archeryTree(playerTraits)
  val cake2: SkillTreeRepr[_] = (new Cake2SkillTrees).archeryTree(playerTraits)
  val ooo: SkillTreeRepr[_] = (new OOOSkillTrees).archeryTree(playerTraits)
}

@BenchmarkMode(Array(SampleTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class Benchmarks {
 // @Benchmark
  def basic(bs: BenchmarkState): Int =  bs.basic.totalCost()

  @Benchmark
  def cake(bs: BenchmarkState): Int =  bs.cake.totalCost()

  @Benchmark
  def cake2(bs: BenchmarkState): Int =  bs.cake2.totalCost()

  //@Benchmark
  def ooo(bs: BenchmarkState): Int =  bs.ooo.totalCost()

//  @Benchmark
  def typeclass(bs: BenchmarkState): Int =  bs.typeclass.totalCost()
}
