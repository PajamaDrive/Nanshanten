package com.example.nanshanten

import android.util.Log

enum class TileGroup {
    //順子
    CHOW{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(index in (0..(hand.size - 1))){
                if(hand[index].getType() <= Tile.Type.BAMBOO) {
                    if(hand[index].getNumber() <= hand[index].getType().getTypeSize() - 2) {
                        if (hand.contains(Tile(hand[index].getType(), hand[index].getNumber() + 1)) &&
                            hand.contains(Tile(hand[index].getType(), hand[index].getNumber() + 2)))
                            return true
                    }
                }
                else
                    return false
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (0..(hand.size - 1))){
                if(hand[index].getType() <= Tile.Type.BAMBOO) {
                    if(hand[index].getNumber() <= hand[index].getType().getTypeSize() - 2) {
                        if (hand.contains(Tile(hand[index].getType(), hand[index].getNumber() + 1)) &&
                            hand.contains(Tile(hand[index].getType(), hand[index].getNumber() + 2))) {
                            var group = mutableListOf<Tile>()
                            group.add(Tile(hand[index].getType(), hand[index].getNumber()))
                            group.add(Tile(hand[index].getType(), hand[index].getNumber() + 1))
                            group.add(Tile(hand[index].getType(), hand[index].getNumber() + 2))
                            groupList.add(group)
                        }
                    }
                }
                else
                    break
            }
            return groupList
        }
    },
    //刻子
    PUNG{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(index in (2..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index]) and hand[index - 2].equals(hand[index]))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (2..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index]) and hand[index - 2].equals(hand[index])){
                    var group = mutableListOf<Tile>()
                    group.add(Tile(hand[index - 2].getType(), hand[index - 2].getNumber()))
                    group.add(Tile(hand[index - 1].getType(), hand[index - 1].getNumber()))
                    group.add(Tile(hand[index].getType(), hand[index].getNumber()))
                    groupList.add(group)
                }
            }
            return groupList
        }
    },
    //槓子
    KONG{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(index in (3..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index]) and hand[index - 2].equals(hand[index]) and hand[index - 3].equals(hand[index]))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (3..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index]) and hand[index - 2].equals(hand[index]) and hand[index - 3].equals(hand[index])){
                    var group = mutableListOf<Tile>()
                    group.add(Tile(hand[index - 3].getType(), hand[index - 3].getNumber()))
                    group.add(Tile(hand[index - 2].getType(), hand[index - 2].getNumber()))
                    group.add(Tile(hand[index - 1].getType(), hand[index - 1].getNumber()))
                    group.add(Tile(hand[index].getType(), hand[index].getNumber()))
                    groupList.add(group)
                }
            }
            return groupList
        }
    },
    //対子
    PAIR{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(index in (1..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index]))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (1..(hand.size - 1))){
                if(hand[index - 1].equals(hand[index])){
                    var group = mutableListOf<Tile>()
                    group.add(Tile(hand[index - 1].getType(), hand[index - 1].getNumber()))
                    group.add(Tile(hand[index].getType(), hand[index].getNumber()))
                    groupList.add(group)
                }
            }
            return groupList
        }
    },
    //字牌
    HONOR{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //数牌
    SUIT{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() <= Tile.Type.BAMBOO)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() <= Tile.Type.BAMBOO)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //同じ数字の数牌
    NUMBER{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() <= Tile.Type.BAMBOO)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (1..9)) {
                var group = mutableListOf<Tile>()
                if (hand.find { it.getType() <= Tile.Type.BAMBOO && it.getNumber() == index } != null)
                    group.addAll(hand.filter { it.getType() <= Tile.Type.BAMBOO && it.getNumber() == index }.toMutableList())
                groupList.add(group)
            }
            return groupList
        }
    },
    //同種の牌
    TYPE{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
                return true
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(type in Tile.Type.values()) {
                var group = mutableListOf<Tile>()
                if (hand.find { it.getType() == type} != null)
                    group.addAll(hand.filter { it.getType() == type }.toMutableList())
                groupList.add(group)
            }
            return groupList
        }
    },
    //ヤオチュー牌
    TERMINAL_AND_HONOR{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO || (tile.getNumber() == 1 || tile.getNumber() == 9))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO || (tile.getNumber() == 1 || tile.getNumber() == 9))
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //萬子
    CHARACTER{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() == Tile.Type.CHARACTER)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() == Tile.Type.CHARACTER)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //筒子
    CIRCLE{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() == Tile.Type.CIRCLE)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() == Tile.Type.CIRCLE)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //索子
    BAMBOO{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() == Tile.Type.BAMBOO)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() == Tile.Type.BAMBOO)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //風牌
    WIND{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() == Tile.Type.WIND)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() == Tile.Type.WIND)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //三元牌
    DRAGON{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() == Tile.Type.DRAGON)
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() == Tile.Type.DRAGON)
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //全ての順番
    STRAIGHT{
        fun containAllNumber(type: Tile.Type, hand: MutableList<Tile>): Boolean{
            for(index in (1..type.getTypeSize())){
                if(hand.find{ it.getType() == type && it.getNumber() == index } == null)
                    return false
            }
            return true
        }
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(type in Tile.Type.values()){
                if(containAllNumber(type, hand))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(type in Tile.Type.values()){
                if(type <= Tile.Type.BAMBOO && containAllNumber(type, hand)) {
                    var group = mutableListOf<Tile>()
                    for (index in (1..type.getTypeSize()))
                        group.add(Tile(type, index))
                    groupList.add(group)
                }
            }
            return groupList
        }
    };

    val groupList = mutableListOf<MutableList<Tile>>()

    abstract fun existGroup(hand: MutableList<Tile>): Boolean
    abstract fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>>
    fun getGroupListNum(hand: MutableList<Tile>): Int{
        getGroupList(hand)
        return groupList.size
    }

}