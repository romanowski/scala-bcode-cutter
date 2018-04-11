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
import rpg._
import rpg.cake2.Cake2SkillTrees
import rpg.cake.CakeSkillTrees
import rpg.ooo.OOOSkillTrees
import rpg.patmat._
import rpg.patmat.stepped.SteppedPatMatSkillTrees
import rpg.typeclass.naive.TypeclassSkillTrees

class BaselineSimple(traits: PlayerTraits) extends SkillTreeRepr {
	override def totalCost(): Int =
		traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost
}


@State(Scope.Benchmark)
class BenchmarkState(val playerTraits: PlayerTraits) {
	def this(){
		this(new PlayerTraits(100, 100, 100))
	}

  val patMat: SkillTreeRepr[_] = (new PatMatSkillTrees).archeryTree(playerTraits)
  val slowPatMat: SkillTreeRepr[_] = (new SlowPathMatSkillTrees).archeryTree(playerTraits)
	val classesPatMat: SkillTreeRepr[_] = (new classes.PathMatSkillTrees).archeryTree(playerTraits)
	val steppedPatMat: SkillTreeRepr[_] = (new SteppedPatMatSkillTrees).archeryTree(playerTraits)
	val steppedClassPatMat: SkillTreeRepr[_] = (new steppedClass.ImplSkillTrees).archeryTree(playerTraits)
  val typeclass: SkillTreeRepr[_] = (new TypeclassSkillTrees).archeryTree(playerTraits)
  val cake: SkillTreeRepr[_] = (new CakeSkillTrees).archeryTree(playerTraits)
  val cake2: SkillTreeRepr[_] = (new Cake2SkillTrees).archeryTree(playerTraits)

	val oooTree: SkillTreeRepr[_] = (new OOOSkillTrees).archeryTree(playerTraits)
	val oooSubcalls: SkillTreeRepr[_] = (new ooo.subcalls.OOOSkillTrees).archeryTree(playerTraits)
	val oooLambdas: SkillTreeRepr[_] = (new ooo.lambdas.LambdasSkillTrees).archeryTree(playerTraits)

	val oooNested: SkillTreeRepr[_] = (new ooo.nested.OOOSkillTrees).archeryTree(playerTraits)
	val javaOOO: SkillTreeRepr[_] = (new java.ooo.SkillTrees).archeryTree(playerTraits)
	val javafast: SkillTreeRepr[_] = (new java.fast.SkillTrees).archeryTree(playerTraits)

	val baseline = new BaselineSimple(playerTraits)
}


trait Benchmarks {
	@Benchmark def patMat(bs: BenchmarkState): Int =  bs.patMat.totalCost()
	@Benchmark def slowPatMat(bs: BenchmarkState): Int =  bs.slowPatMat.totalCost()
	@Benchmark def classesPatMat(bs: BenchmarkState): Int =  bs.classesPatMat.totalCost()
	@Benchmark def steppedPatMat(bs: BenchmarkState): Int =  bs.steppedPatMat.totalCost()
	@Benchmark def steppedClassPatMat(bs: BenchmarkState): Int =  bs.steppedClassPatMat.totalCost()
	@Benchmark def typeclass(bs: BenchmarkState): Int =  bs.typeclass.totalCost()
	@Benchmark def cake(bs: BenchmarkState): Int =  bs.cake.totalCost()
	@Benchmark def cake2(bs: BenchmarkState): Int =  bs.cake2.totalCost()

	@Benchmark def oooTree(bs: BenchmarkState): Int =  bs.oooTree.totalCost()
	@Benchmark def oooSubcalls(bs: BenchmarkState): Int =  bs.oooSubcalls.totalCost()
	@Benchmark def oooLambdas(bs: BenchmarkState): Int =  bs.oooLambdas.totalCost()

	@Benchmark def oooNested(bs: BenchmarkState): Int =  bs.oooNested.totalCost()
	@Benchmark def javaOOO(bs: BenchmarkState): Int =  bs.javaOOO.totalCost()
	@Benchmark def javaFast(bs: BenchmarkState): Int =  bs.javafast.totalCost()

	@Benchmark def baseline(bs: BenchmarkState): Int =  {
		val traits = bs.playerTraits
		traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost + traits.strCost + traits.wisCost +
			traits.dexCost
	}
	// @Benchmark def baseline3(bs: BenchmarkState): Int =  bs.baseline.totalCost()
}


@BenchmarkMode(Array(SampleTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class HotBenchmark extends Benchmarks

@BenchmarkMode(Array(SingleShotTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1, batchSize = 20)
@Measurement(iterations = 5000, batchSize = 50)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class WarmingBenchmark extends Benchmarks
//
//@BenchmarkMode(Array(SampleTime))
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 30, time = 200, timeUnit = TimeUnit.MICROSECONDS)
//@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MICROSECONDS)
//@Fork(value = 2, jvmArgs = Array("-Xms2G", "-Xmx2G"))
//class SemiHotBenchmark extends Benchmarks

//@BenchmarkMode(Array(SingleShotTime))
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Measurement(iterations = 1024)
//@Warmup(iterations = 1024)
//@Fork(value = 2, jvmArgs = Array("-Xms2G", "-Xmx2G"))
//class ColdBenchmark extends Benchmarks


