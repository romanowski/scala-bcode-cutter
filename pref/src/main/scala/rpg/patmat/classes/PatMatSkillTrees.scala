package rpg.patmat.classes

import rpg._

sealed class Node

case class DexTree(left: Node, right: Node) extends Node

case class StrTree(left: Node, right: Node) extends Node

case class WisTree(left: Node, right: Node) extends Node

case class DexStep(to: Node) extends Node

case class StrStep(to: Node) extends Node

case class WisStep(to: Node) extends Node

case class DexSkill(name: String) extends Node

case class StrSkill(name: String) extends Node

case class WisSkill(name: String) extends Node

object Node {
  def totalSkill(node: Node, traits: PlayerTraits): Int = node match {
    case DexTree(left, right) =>
      traits.dexCost + totalSkill(left, traits) + totalSkill(right, traits)
    case StrTree(left, right) =>
      traits.strCost + totalSkill(left, traits) + totalSkill(right, traits)
    case WisTree(left, right) =>
      traits.wisCost + totalSkill(left, traits) + totalSkill(right, traits)

    case DexStep(next) =>
      traits.dexCost + totalSkill(next, traits)
    case StrStep(next) =>
      traits.strCost + totalSkill(next, traits)
    case WisStep(next) =>
      traits.wisCost + totalSkill(next, traits)

    case DexSkill(_) =>
      traits.dexCost
    case StrSkill(_) =>
      traits.strCost
    case WisSkill(_) =>
      traits.wisCost
  }

  def slowTotalSkill(node: Node, traits: PlayerTraits): Int = node match {

    case StrSkill(_) =>
      traits.strCost
    case WisSkill(_) =>
      traits.wisCost
    case DexSkill(_) =>
      traits.dexCost

    case StrStep(next) =>
      traits.strCost + totalSkill(next, traits)
    case WisStep(next) =>
      traits.wisCost + totalSkill(next, traits)
    case DexStep(next) =>
      traits.dexCost + totalSkill(next, traits)

    case StrTree(left, right) =>
      traits.strCost + totalSkill(left, traits) + totalSkill(right, traits)
    case WisTree(left, right) =>
      traits.wisCost + totalSkill(left, traits) + totalSkill(right, traits)
    case DexTree(left, right) =>
      traits.dexCost + totalSkill(left, traits) + totalSkill(right, traits)



  }
}

trait TreeProvider {
  /**
    * Dex --> Dex -> Wis --> Wis("aimed")
    * \                   \-> Dex("rapid")
    * \-> Str --> Str("power")
    * \-> Str -> Dex("double")
    */
  def mkTree = DexTree(
    DexStep(WisTree(
      WisSkill("aimed"),
      WisStep(DexSkill("rapid"))
    )),
    StrTree(
      StrSkill("power"),
      StrStep(DexSkill("double"))
    )
  )
}

class PatMatSkillTrees extends SkillTrees with TreeProvider {
  override def archeryTree(traits: PlayerTraits): SkillTreeRepr[_] = {
    new SkillTreeRepr(mkTree) {
      override def totalCost(): Int = Node.totalSkill(tree, traits)
    }
  }
}

class PathMatSkillTrees extends SkillTrees with TreeProvider {
  override def archeryTree(traits: PlayerTraits): SkillTreeRepr[_] = {
    new SkillTreeRepr(mkTree) {
      override def totalCost(): Int = Node.slowTotalSkill(tree, traits)
    }
  }
}