package com.example.nanshanten

import android.util.Log
import java.text.SimpleDateFormat

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
            if(pairList.size == 7){
                yakus.add("七対子")
                if(TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14)
                    yakus.add("字一色")
            }
            for(pair in pairList) {
                val removePairHand = hand.getRemoveList(pair)
                val kongList = TileGroup.KONG.getGroupList(removePairHand).distinctBy { it.get(0).toString() }.toMutableList()
                var kongCombination = combination(kongList, mutableListOf(mutableListOf(mutableListOf())))
                for (kong in kongCombination) {
                    val removeKongList = kong.flatten().toMutableList()
                    removeKongList.addAll(pair.toMutableList())
                    val removeKongHand = hand.getRemoveList(removeKongList)
                    val pungList = TileGroup.PUNG.getGroupList(removeKongHand).distinctBy { it.get(0).toString() }.toMutableList()
                    var pungCombination = combination(pungList, mutableListOf(mutableListOf(mutableListOf())))
                    for (pung in pungCombination) {
                        val removePungList = pung.flatten().toMutableList()
                        removePungList.addAll(removeKongList.toMutableList())
                        val removePungHand = hand.getRemoveList(removePungList)
                        val chowList = TileGroup.CHOW.getGroupList(removePungHand)
                        var chowCombination = combination(chowList, mutableListOf(mutableListOf(mutableListOf())))
                        Log.d("afterdistinct", chowCombination.toString())
                        chowCombination = chowCombination.filter { hand.canRemove(it.flatten().toMutableList()) &&
                                hand.getRemoveList(removePungList.plus(it.flatten()).toMutableList()).size == 0 }.toMutableList()
                        Log.d("afterfilter", chowCombination.toString())
                        //Log.d("afterFilter", SimpleDateFormat("HH:mm:ss:SSSS").format(System.currentTimeMillis()).toString())
                        for(chow in chowCombination){
                            val removeChowList = chow.flatten().toMutableList()
                            removeChowList.addAll(removePungList.toMutableList())
                            if (pung.size == 4) {
                                yakus.add("対々和")
                                yakus.add("四暗刻")
                                if (TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14)
                                    yakus.add("字一色")
                                if (TileGroup.WIND.getGroupListNum(pung.flatten().toMutableList()) == 12)
                                    yakus.add("大四喜")
                                if (TileGroup.TERMINAL_AND_HONOR.getGroupListNum(hand.getSortHand()) == 14) {
                                    if (TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 0)
                                        yakus.add("清老頭")
                                    else
                                        yakus.add("混老頭")
                                }
                            }
                            if (pung.size >= 3) {
                                yakus.add("三暗刻")
                                if (TileGroup.DRAGON.getGroupListNum(pung.flatten().toMutableList()) == 9)
                                    yakus.add("大三元")
                                if (TileGroup.WIND.getGroupListNum(hand.getSortHand()) == 11)
                                    yakus.add("小四喜")
                                if(TileGroup.NUMBER.getGroupList(pung.flatten().toMutableList()).maxBy { it.size }!!.size == 9)
                                    yakus.add("三色同刻")
                            }
                            if(pung.size >= 2){
                                if (TileGroup.DRAGON.getGroupListNum(pung.flatten().toMutableList()) == 8)
                                    yakus.add("小三元")
                            }

                            if(chow.size >= 3){
                                if(chow.distinctBy { it.get(0).getType() }.groupingBy{ it.get(0).getNumber() }.eachCount().values.max() == 3)
                                    yakus.add("三色同順")
                            }

                            if(chow.size >= 2){
                                val chowSize = TileGroup.TYPE.getGroupList(chow.distinctBy{ it.get(0).toString() }.flatten().toMutableList()).map{ it.size }
                                val maxDistinctSize = TileGroup.TYPE.getGroupList(chow.flatten().toMutableList()).map{ it.size }.mapIndexed { index, i -> i - chowSize[index]}.max()
                                if(chow.size - chow.distinctBy{ it.get(0).toString() }.size == 1)
                                    yakus.add("一盃口")
                                if(chow.size - chow.distinctBy{ it.get(0).toString() }.size == 3)
                                    yakus.add("二盃口")
                                if(chow.size - chow.distinctBy{ it.get(0).toString() }.size == 2) {
                                    if (maxDistinctSize == 3)
                                        yakus.add("二盃口")
                                    else
                                        yakus.add("一盃口")
                                }
                            }

                            if (TileGroup.BAMBOO.getGroupListNum(hand.getSortHand()) >= 11){
                                if(hand.getSortHand().count{it.equals(Tile(Tile.Type.BAMBOO, 2))} + hand.getSortHand().count{it.equals(Tile(Tile.Type.BAMBOO, 3))}
                                    + hand.getSortHand().count{it.equals(Tile(Tile.Type.BAMBOO, 4))} + hand.getSortHand().count{it.equals(Tile(Tile.Type.BAMBOO, 6))}
                                    + hand.getSortHand().count{it.equals(Tile(Tile.Type.BAMBOO, 8))}  + hand.getSortHand().count{it.equals(Tile(Tile.Type.DRAGON, 2))}  == 14)
                                    yakus.add("緑一色")
                            }

                            if(TileGroup.TYPE.getGroupList(hand.getSortHand()).maxBy { it.size }!!.size == 14 && hand.getSortHand()[0].getType() <= Tile.Type.BAMBOO) {
                                yakus.add("清一色")
                            }
                            else if(TileGroup.CHARACTER.getGroupListNum(hand.getSortHand()) + TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14 ||
                                TileGroup.CIRCLE.getGroupListNum(hand.getSortHand()) + TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14 ||
                                TileGroup.BAMBOO.getGroupListNum(hand.getSortHand()) + TileGroup.HONOR.getGroupListNum(hand.getSortHand()) == 14) {
                                yakus.add("混一色")
                            }

                            if(TileGroup.TERMINAL_AND_HONOR.getGroupListNum(removePungList) == removePungList.size)
                                if(chow.get(0).isEmpty()) {
                                    if (TileGroup.SUIT.getGroupListNum(hand.getSortHand()) == 14)
                                        yakus.add("純全帯么九")
                                    else
                                        yakus.add("混全帯么九")
                                }else if(TileGroup.TERMINAL_AND_HONOR.getGroupListNum(chow.flatten().toMutableList()) == chow.size){
                                    if (TileGroup.SUIT.getGroupListNum(hand.getSortHand()) == 14)
                                        yakus.add("純全帯么九")
                                    else
                                        yakus.add("混全帯么九")
                            }

                            if(TileGroup.TERMINAL_AND_HONOR.getGroupListNum(hand.getSortHand()) == 0){
                                yakus.add("断么九")
                            }

                            if(TileGroup.SUIT.getGroupListNum((pair)) == 2){
                                if(chow.size == 4){
                                    yakus.add("平和")
                                }
                            }

                            if(TileGroup.STRAIGHT.getGroupListNum(hand.getSortHand()) >= 1){
                                yakus.add("一気通貫")
                                if(TileGroup.TYPE.getGroupList(hand.getSortHand()).maxBy { it.size }!!.size == 14 && hand.getSortHand()[0].getType() <= Tile.Type.BAMBOO){
                                    if(hand.getSortHand().count{it.getNumber() == 1} + hand.getSortHand().count{it.getNumber() == 9} >= 6)
                                        yakus.add("九蓮宝燈")
                                }
                            }
                        }
                    }
                }
            }
            return yakus.distinct().toMutableList()
        }

        fun combination(groupList: MutableList<MutableList<Tile>>, resultGroupList: MutableList<MutableList<MutableList<Tile>>>): MutableList<MutableList<MutableList<Tile>>>{
            val distinctGroup = groupList.distinctBy { it.get(0).toString() }.toMutableList()

            if(groupList.size == 0)
                return resultGroupList
            else{
                val originalResult = resultGroupList.toMutableList()
                for (i in (0..(distinctGroup.size - 1))) {
                    val tileGroup = distinctGroup.filterIndexed { index, mutableList -> index <= i }.toMutableList()
                    val removedGroupList = distinctGroup.toMutableList()
                    for (tiles in tileGroup) {
                        removedGroupList.remove(tiles)
                    }
                    val previousResult = originalResult.toMutableList()
                    for (j in (0..(previousResult.size - 1))) {
                        //val temp = previousResult.toMutableList().map{ it.plusElement(tileGroup.last()).toMutableList() }.toMutableList()
                        val temp = previousResult.toMutableList()
                        for (k in (0..(previousResult.get(j).size - 1))) {
                            if (previousResult.get(j).get(k).isEmpty())
                                temp.set(j, mutableListOf(tileGroup.last()))
                            else if(previousResult.get(j).size < 4)
                                temp.set(j, previousResult.get(j).plus(mutableListOf(tileGroup.last())).toMutableList())
                        }

                        previousResult.add(temp.get(j))
                    }

/*
                    val duplication = groupList.count { it.get(0).toString() == distinctGroup.get(i).get(0).toString() }

                    if(duplication >= 2){
                        var temp = previousResult.toMutableList()
                        for(k in (2..duplication)){
                            temp = temp.map{ it.plusElement(distinctGroup.get(i)).toMutableList() }.filter{ it.size < 5 }.toMutableList()
                            previousResult.addAll(temp)
                            originalResult.addAll(temp)
                        }
                    }

 */


                    resultGroupList.addAll(combination(removedGroupList, previousResult).filter{ it.size < 5 && resultGroupList.indexOf(it) == -1 }.toMutableList())

                    //resultGroupList.addAll(previousResult)
                }
            }
            for (i in (0..(distinctGroup.size - 1))) {
                val duplication = groupList.count { it.get(0).toString() == distinctGroup.get(i).get(0).toString() }

                if(duplication >= 2){
                    var temp = resultGroupList.toMutableList()
                    for(k in (2..duplication)){
                        temp = temp.map{ it.plusElement(distinctGroup.get(i)).toMutableList() }.filter{ it.size < 5 }.toMutableList()
                        resultGroupList.addAll(temp)
                    }
                }
            }
            return resultGroupList
        }

        fun getYakuList(hand: Hand): MutableList<String>{
            yakuList.clear()
            if(thirteenOrphans(hand))
                yakuList.add("国士無双")
            yakuList.addAll(containPungYaku(hand))
            return yakuList
        }
    }
}