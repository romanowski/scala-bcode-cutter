package rpg.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode.SampleTime
import org.openjdk.jmh.annotations.Mode.SingleShotTime
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import rpg._
import rpg.cake2.Cake2SkillTrees
import rpg.cake.CakeSkillTrees
import rpg.ooo.OOOSkillTrees
import rpg.patmat._
import rpg.patmat.stepped.SteppedPatMatSkillTrees
import rpg.patmat.wrapped.WrappedTrySkillTrees
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

	@Param(Array("0", "1"))
	var state: Int = 0

	private def trees(provider:SkillTrees): Array[SkillTreeRepr[_]] = Array(provider.archeryTree(playerTraits), provider.charismaTree(playerTraits))

  val patMat = trees(new PatMatSkillTrees)
	val slowPatMat = trees(new SlowPathMatSkillTrees)
	val classesPatMat = trees(new classes.PathMatSkillTrees)
	val steppedPatMat = trees(new SteppedPatMatSkillTrees)
	val steppedClassPatMat = trees(new steppedClass.ImplSkillTrees)
  val typeclass = trees(new TypeclassSkillTrees)
  val cake = trees(new CakeSkillTrees)
 	val cake2 = trees(new Cake2SkillTrees)

	val oooTree = trees(new OOOSkillTrees)
	val oooSubcalls = trees(new ooo.subcalls.OOOSkillTrees)
	val oooLambdas = trees(new ooo.lambdas.LambdasSkillTrees)

	val oooNested = trees(new ooo.nested.OOOSkillTrees)
	val javaOOO = trees(new java.ooo.SkillTrees)
	val javafast = trees(new java.fast.SkillTrees)

	val wrappedInTry = trees(new WrappedTrySkillTrees)

	val baseline = new BaselineSimple(playerTraits)
}


class BBase {

	@Benchmark def patMat(bs: BenchmarkState): Int =  bs.patMat(bs.state).totalCost()
	@Benchmark def slowPatMat(bs: BenchmarkState): Int =  bs.slowPatMat(bs.state).totalCost()
	@Benchmark def classesPatMat(bs: BenchmarkState): Int =  bs.classesPatMat(bs.state).totalCost()
	@Benchmark def steppedPatMat(bs: BenchmarkState): Int =  bs.steppedPatMat(bs.state).totalCost()
	@Benchmark def steppedClassPatMat(bs: BenchmarkState): Int =  bs.steppedClassPatMat(bs.state).totalCost()

	@Benchmark def typeclass(bs: BenchmarkState): Int =  bs.typeclass(bs.state).totalCost()
	@Benchmark def cake(bs: BenchmarkState): Int =  bs.cake(bs.state).totalCost()
	@Benchmark def cake2(bs: BenchmarkState): Int =  bs.cake2(bs.state).totalCost()

	@Benchmark def oooTree(bs: BenchmarkState): Int =  bs.oooTree(bs.state).totalCost()
	@Benchmark def oooSubcalls(bs: BenchmarkState): Int =  bs.oooSubcalls(bs.state).totalCost()
	@Benchmark def oooLambdas(bs: BenchmarkState): Int =  bs.oooLambdas(bs.state).totalCost()

	@Benchmark def oooNested(bs: BenchmarkState): Int =  bs.oooNested(bs.state).totalCost()
	@Benchmark def javaOOO(bs: BenchmarkState): Int =  bs.javaOOO(bs.state).totalCost()
	@Benchmark def javaFast(bs: BenchmarkState): Int =  bs.javafast(bs.state).totalCost()

	@Benchmark def wrappedInTry(bs: BenchmarkState): Int =  bs.wrappedInTry(bs.state).totalCost()

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
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
class HotBenchmark extends BBase

@BenchmarkMode(Array(SingleShotTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1, batchSize = 20)
@Measurement(iterations = 5000, batchSize = 50)
@Fork(value = 1, jvmArgs = Array("-Xms2G", "-Xmx2G"))
@State(Scope.Benchmark)
class WarmingBenchmark extends BBase
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


