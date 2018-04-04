package rpg.cake2

import rpg.PlayerTraits
import rpg.SkillTreeRepr
import rpg.SkillTrees

trait WisNodes  { self: NodesTraits =>
  case class WisTree(left: Node, right: Node) extends Node
  case class WisStep(to: Node) extends Node
  case class WisSkill(name: String) extends Node
}

trait DexNodes { self: NodesTraits =>
  case class DexSkill(name: String) extends Node
  case class DexStep(to: Node) extends Node
  case class DexTree(left: Node, right: Node) extends Node
}

trait StrNodes { self: NodesTraits =>
  case class StrTree(left: Node, right: Node) extends Node
  case class StrStep(to: Node) extends Node
  case class StrSkill(name: String) extends Node
}

trait Traits {
  def traits: PlayerTraits
}

trait NodesTraits extends WisNodes with DexNodes with StrNodes with Traits {
  sealed trait Node

  def totalSkill(node: Node): Int = node match {
    case DexTree(left, right) =>
      traits.dexCost + totalSkill(left) + totalSkill(right)
    case StrTree(left, right) =>
      traits.strCost + totalSkill(left) + totalSkill(right)
    case WisTree(left, right) =>
      traits.wisCost + totalSkill(left) + totalSkill(right)

    case DexStep(next) =>
      traits.dexCost + totalSkill(next)
    case StrStep(next) =>
      traits.strCost + totalSkill(next)
    case WisStep(next) =>
      traits.wisCost + totalSkill(next)

    case DexSkill(_) =>
      traits.dexCost
    case StrSkill(_) =>
      traits.strCost
    case WisSkill(_) =>
      traits.wisCost
  }
}

class Cake2SkillTrees extends SkillTrees {
  /**
    * Dex --> Dex -> Wis --> Wis("aimed")
    * \                   \-> Dex("rapid")
    * \-> Str --> Str("power")
    * \-> Str -> Dex("double")
    */
  override def archeryTree(playerTraits: PlayerTraits): SkillTreeRepr[_] = {
    val nodes = new  NodesTraits {
      override def traits: PlayerTraits = playerTraits
    }
    import nodes._
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
      override def totalCost(): Int = totalSkill(tree)
    }

  }
}