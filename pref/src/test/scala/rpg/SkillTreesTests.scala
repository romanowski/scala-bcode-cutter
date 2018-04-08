package rpg

import org.scalatest._
import rpg.bench.BenchmarkState
import rpg.bench.Benchmarks
import rpg.cake2.Cake2SkillTrees
import rpg.cake.CakeSkillTrees
import rpg.patmat.PatMatSkillTrees
import rpg.typeclass.naive.TypeclassSkillTrees

class SkillTreesTests extends FlatSpec with Matchers {

	case object EqualTraits extends PlayerTraits(100, 100, 100)

	case object StrTraits extends PlayerTraits(100, 0, 0)

	case object DexTraits extends PlayerTraits(0, 100, 0)

	case object WisTraits extends PlayerTraits(0, 0, 100)

	val methods = classOf[BenchmarkState].getMethods.filter(_.getReturnType == classOf[SkillTreeRepr[_]])

	for {
		method <- methods
		(traits, expected) <- Seq(
			EqualTraits -> 1000,
			StrTraits -> 300,
			DexTraits -> 400,
			WisTraits -> 300
		)
	} s"Benchmarks named ${method.getName} for $traits" should "work" in {
		val state = new BenchmarkState(traits)
		method.invoke(state).asInstanceOf[SkillTreeRepr[_]].totalCost() shouldBe expected
	}
}
