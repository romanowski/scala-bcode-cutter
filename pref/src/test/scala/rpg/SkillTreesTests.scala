package rpg

import org.scalatest._
import rpg.bench.BenchmarkState
import rpg.bench.Benchmarks
import rpg.cake2.Cake2SkillTrees
import rpg.cake.CakeSkillTrees
import rpg.patmat.PatMatSkillTrees
import rpg.typeclass.naive.TypeclassSkillTrees

class SkillTreesTests extends FlatSpec with Matchers {

  object EqualTraits extends PlayerTraits(100, 100, 100)
  object StrTraits extends PlayerTraits(100, 0, 0)
  object DexTraits extends PlayerTraits(0, 100, 0)
  object WisTraits extends PlayerTraits(0, 0, 100)

  def testTress(trees: SkillTrees): Unit ={
    trees.archeryTree(EqualTraits).totalCost() shouldBe 1000
    trees.archeryTree(StrTraits).totalCost shouldBe 300
    trees.archeryTree(DexTraits).totalCost shouldBe 400
    trees.archeryTree(WisTraits).totalCost shouldBe 300
  }

  "Typeclass skill tress" should "be computed correctly" in  testTress(new TypeclassSkillTrees)
  "Basic skill tress" should "be computed correctly" in  testTress(new PatMatSkillTrees)
  "Cake skill tress" should "be computed correctly" in  testTress(new CakeSkillTrees)
  "Cake2 skill tress" should "be computed correctly" in  testTress(new Cake2SkillTrees)

  "Benchmarks" should "work" in {
    val state = new BenchmarkState

    def test(method: BenchmarkState => Int) = {
      method(state) shouldBe 1000
    }

    val bench = new Benchmarks {}
    test(bench.patMat)
    test(bench.slowPatMat)
    test(bench.steppedPatMat)
    test(bench.cake)
    test(bench.cake2)
    test(bench.ooo)
    test(bench.typeclass)
  }

}
