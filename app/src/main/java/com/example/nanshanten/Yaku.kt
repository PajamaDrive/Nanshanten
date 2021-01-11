package com.example.nanshanten

import android.util.Log

class Yaku{
    companion object {
        private val yakuList = mutableListOf<String>()
        fun thirteenOrphans(hand: Hand): Boolean {
            if(hand.existTile(Tile(Tile.Type.CHARACTER, 1)) and hand.existTile(Tile(Tile.Type.CHARACTER, 9)) and
                hand.existTile(Tile(Tile.Type.CIRCLE, 1)) and hand.existTile(Tile(Tile.Type.CIRCLE, 9)) and
                hand.existTile(Tile(Tile.Type.BAMBOO, 1)) and hand.existTile(Tile(Tile.Type.BAMBOO, 9)) and
                hand.existTile(Tile(Tile.Type.WIND, 1)) and hand.existTile(Tile(Tile.Type.WIND, 2)) and
                hand.existTile(Tile(Tile.Type.WIND, 3)) and hand.existTile(Tile(Tile.Type.WIND, 4)) and
                hand.existTile(Tile(Tile.Type.DRAGON, 1)) and hand.existTile(Tile(Tile.Type.DRAGON, 2)) and
                hand.existTile(Tile(Tile.Type.DRAGON, 3)))
                if(TileGroup.PAIR.existGroup(hand.getSortHand()))
                    return true
            return false
        }

        fun allGreen(hand: Hand): Boolean {
            return false
        }

        fun yakuOfPairs(hand: Hand): MutableList<String>{
            val yakus = mutableListOf<String>()
            val group = TileGroup.PAIR.getGroupList(hand.getSortHand()).distinctBy { it.get(0).toString() }
            if(group.size == 7){
                yakus.add("七対子")
                if(TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14)
                    yakus.add("字一色")
            }
            return yakus
        }

        fun containPungYaku(hand: Hand): MutableList<String>{
            val yakus = mutableListOf<String>()
            val pairList = TileGroup.PAIR.getGroupList(hand.getSortHand()).distinctBy { it.get(0).toString() }
            for(pair in pairList){
                val removePairHand = hand.getRemoveList(pair)
                val kongList = TileGroup.KONG.getGroupList(removePairHand).distinctBy { it.get(0).toString() }.toMutableList()
                var kongCombination = mutableListOf(mutableListOf(mutableListOf<Tile>()))
                for(i in (0..(kongList.size - 1))){
                    kongCombination.add(mutableListOf(kongList.get(i)))
                    for(j in ((i + 1)..(kongList.size - 1))) {
                        kongCombination.add(mutableListOf(kongList.get(i), kongList.get(j)))
                        for(k in ((j + 1)..(kongList.size - 1))) {
                            kongCombination.add(mutableListOf(kongList.get(i), kongList.get(j), kongList.get(k)))
                        }
                    }
                }
                for(kong in kongCombination){
                    val removeKongList = kong.flatten().toMutableList()
                    removeKongList.addAll(pair.toMutableList())
                    val removeKongHand = hand.getRemoveList(removeKongList)
                    val pungList = TileGroup.PUNG.getGroupList(removeKongHand).distinctBy { it.get(0).toString() }.toMutableList()
                    var pungCombination = mutableListOf(mutableListOf(mutableListOf<Tile>()))
                    for(i in (0..(pungList.size - 1))){
                        pungCombination.add(mutableListOf(pungList.get(i)))
                        for(j in ((i + 1)..(pungList.size - 1))) {
                            pungCombination.add(mutableListOf(pungList.get(i), pungList.get(j)))
                            for(k in ((j + 1)..(pungList.size - 1))) {
                                pungCombination.add(mutableListOf(pungList.get(i), pungList.get(j), pungList.get(k)))
                                for(l in ((k + 1)..(pungList.size - 1))) {
                                    pungCombination.add(mutableListOf(pungList.get(i), pungList.get(j), pungList.get(k), pungList.get(l)))
                                }
                            }
                        }
                    }
                    Log.d("debug1", pungCombination.toString())
                    Log.d("debug2", combination(pungList, mutableListOf(mutableListOf(mutableListOf()))).toString())
                    for(pung in pungCombination){
                        val removePungList = pung.flatten().toMutableList()
                        removePungList.addAll(removeKongList.toMutableList())
                        val removePungHand = hand.getRemoveList(removePungList)
                        val chowList = TileGroup.CHOW.getGroupList(removePungHand)
                        var chowCombination = mutableListOf(mutableListOf(mutableListOf<Tile>()))

                    }
                }


            }
            return yakus
        }

        fun combination(groupList: MutableList<MutableList<Tile>>, resultGroupList: MutableList<MutableList<MutableList<Tile>>>): MutableList<MutableList<MutableList<Tile>>>{
            if(groupList.size == 0)
                return resultGroupList
            else{
                val originalResult = resultGroupList.toMutableList()
                for (i in (0..(groupList.size - 1))) {
                    val tileGroup = groupList.filterIndexed { index, mutableList -> index <= i }.toMutableList()
                    val removedGroupList = groupList.toMutableList()
                    val tempResultList = originalResult.toMutableList()
                    for (tiles in tileGroup) {
                        removedGroupList.remove(tiles)
                    }
                    val previousResult = tempResultList.toMutableList()
                    for (j in (0..(previousResult.size - 1))) {
                        val temp = previousResult.toMutableList()
                        for (k in (0..(previousResult.get(j).size - 1))) {
                            if (previousResult.get(j).get(k).isEmpty())
                                temp.set(j, mutableListOf(tileGroup.last()))
                            else
                                temp.set(j, previousResult.get(j).plus(mutableListOf(tileGroup.last())).toMutableList())
                        }
                        previousResult.add(temp.get(j))
                    }

                    resultGroupList.addAll(combination(removedGroupList, previousResult))

                    resultGroupList.addAll(previousResult)
                }

            }
            return resultGroupList.distinct().toMutableList()
        }

        fun bigDragons(hand: Hand): Boolean{
            val group = TileGroup.PUNG.getGroupList(hand.getSortHand()).distinctBy { it.get(0).toString() }.filter { it.get(0).getType() == Tile.Type.DRAGON }
            lateinit var distinctDragon: MutableList<Tile>
            for(tiles in group){
                distinctDragon = hand.getRemoveList(tiles)
            }
            TileGroup.PAIR.getGroupList(distinctDragon)

            if(group.size == 7)
                return true
            return false
        }

        fun getYakuList(hand: Hand): MutableList<String>{
            yakuList.clear()
            if(thirteenOrphans(hand))
                yakuList.add("国士無双")
            if(allGreen(hand))
                yakuList.add("緑一色")
            yakuList.addAll(yakuOfPairs(hand))
            yakuList.addAll(containPungYaku(hand))
            return yakuList
        }
    }
}