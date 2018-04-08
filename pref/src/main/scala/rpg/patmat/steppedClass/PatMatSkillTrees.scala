package rpg.patmat.steppedClass

import rpg._

sealed class Node

sealed class Tree extends Node
case class DexTree(left: Node, right: Node) extends Tree

case class StrTree(left: Node, right: Node) extends Tree

case class WisTree(left: Node, right: Node) extends Tree

sealed class Step extends Node

case class DexStep(to: Node) extends Step

case class StrStep(to: Node) extends Step

case class WisStep(to: Node) extends Step

sealed class Skill extends Node

case class DexSkill(name: String) extends Skill

case class StrSkill(name: String) extends Skill

case class WisSkill(name: String) extends Skill

object Node {
  def totalSkill(node: Node, traits: PlayerTraits): Int = node match {
    case tree: Tree => tree match {
      case DexTree(left, right) =>
        traits.dexCost + totalSkill(left, traits) + totalSkill(right, traits)
      case StrTree(left, right) =>
        traits.strCost + totalSkill(left, traits) + totalSkill(right, traits)
      case WisTree(left, right) =>
        traits.wisCost + totalSkill(left, traits) + totalSkill(right, traits)
    }
    case step: Step => step match {
      case DexStep(next) =>
        traits.dexCost + totalSkill(next, traits)
      case StrStep(next) =>
        traits.strCost + totalSkill(next, traits)
      case WisStep(next) =>
        traits.wisCost + totalSkill(next, traits)
    }
    case skill: Skill => skill match {
      case DexSkill(_) =>
        traits.dexCost
      case StrSkill(_) =>
        traits.strCost
      case WisSkill(_) =>
        traits.wisCost
    }
  }
}


class ImplSkillTrees extends SkillTrees {


  /**
    * Dex --> Dex -> Wis --> Wis("aimed")
    * \                   \-> Dex("rapid")
    * \-> Str --> Str("power")
    * \-> Str -> Dex("double")
    */
  override def archeryTree(traits: PlayerTraits): SkillTreeRepr[_] = {
    val tree = DexTree(
      DexStep(WisTree(
        WisSkill("aimed"),
        WisStep(DexSkill("rapid"))
      )),
      StrTree(
        StrSkill("power"),
        StrStep(DexSkill("double"))
      )
    )
    new SkillTreeRepr(tree) {
      override def totalCost(): Int = Node.totalSkill(tree, traits)
    }

  }
}
