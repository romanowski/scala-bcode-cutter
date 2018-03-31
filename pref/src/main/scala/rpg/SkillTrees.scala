package rpg

class PlayerTraits(val strCost: Int, val dexCost: Int, val wisCost: Int)

abstract class SkillTreeRepr[T](val tree: T) {
  def totalCost(): Int
}

abstract class SkillTrees {
  /**
    * Dex --> Dex -> Wis --> Wis("aimed")
    * \                   \-> Wis -> Dex("rapid")
    * \-> Str --> Str("power")
    *          \-> Str -> Dex("double")
    */
  def archeryTree(playerTraits: PlayerTraits): SkillTreeRepr[_]
}