package rpg.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode.SampleTime
import org.openjdk.jmh.annotations.Mode.SingleShotTime
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import rpg.PlayerTraits
import rpg.SkillTreeRepr
import rpg.cake2.Cake2SkillTrees
import rpg.cake.CakeSkillTrees
import rpg.ooo.OOOSkillTrees
import rpg.patmat.PatMatSkillTrees
import rpg.patmat.SlowPathMatSkillTrees
import rpg.patmat.stepped.SteppedPatMatSkillTrees
import rpg.typeclass.naive.TypeclassSkillTrees


@State(Scope.Benchmark)
class BenchmarkState {
  val playerTraits = new PlayerTraits(100, 100, 100)

  val patMat: SkillTreeRepr[_] = (new PatMatSkillTrees).archeryTree(playerTraits)
  val slowPatMat: SkillTreeRepr[_] = (new SlowPathMatSkillTrees).archeryTree(playerTraits)
  val steppedPatMat: SkillTreeRepr[_] = (new SteppedPatMatSkillTrees).archeryTree(playerTraits)

  val typeclass: SkillTreeRepr[_] = (new TypeclassSkillTrees).archeryTree(playerTraits)
  val cake: SkillTreeRepr[_] = (new CakeSkillTrees).archeryTree(playerTraits)
  val cake2: SkillTreeRepr[_] = (new Cake2SkillTrees).archeryTree(playerTraits)
  val ooo: SkillTreeRepr[_] = (new OOOSkillTrees).archeryTree(playerTraits)
}


trait Benchmarks {
  @Benchmark
  def patMat(bs: BenchmarkState): Int =  bs.patMat.totalCost()
//  @Benchmark
  def slowPatMat(bs: BenchmarkState): Int =  bs.slowPatMat.totalCost()
  @Benchmark
  def steppedPatMat(bs: BenchmarkState): Int =  bs.steppedPatMat.totalCost()

  @Benchmark
  def cake(bs: BenchmarkState): Int =  bs.cake.totalCost()

 // @Benchmark
  def cake2(bs: BenchmarkState): Int =  bs.cake2.totalCost()

  @Benchmark
  def ooo(bs: BenchmarkState): Int =  bs.ooo.totalCost()

   @Benchmark
  def typeclass(bs: BenchmarkState): Int =  bs.typeclass.totalCost()
}


@BenchmarkMode(Array(SampleTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class HotBenchmark extends Benchmarks

@BenchmarkMode(Array(SampleTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 30, time = 200, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MICROSECONDS)
@Fork(value = 2, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class SemiHotBenchmark extends Benchmarks

@BenchmarkMode(Array(SingleShotTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 1024)
@Warmup(iterations = 1024)
@Fork(value = 2, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class ColdBenchmark extends Benchmarks

