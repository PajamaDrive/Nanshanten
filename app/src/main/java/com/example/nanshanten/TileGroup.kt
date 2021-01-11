package com.example.nanshanten

import android.util.Log

enum class TileGroup {
    //順子
    CHOW{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(index in (0..(hand.size - 3))){
                if(hand[index].getType() > Tile.Type.BAMBOO) {
                    if ((hand[index + 1].getType() == hand[index].getType() && hand[index + 1].getNumber() == hand[index].getNumber() + 1)
                        && (hand[index + 2].getType() == hand[index].getType() && hand[index + 2].getNumber() == hand[index].getNumber() + 1))
                        return true
                }
                else
                    return false
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(index in (0..(hand.size - 3))){
                if(hand[index].getType() > Tile.Type.BAMBOO) {
                    if ((hand[index + 1].getType() == hand[index].getType() && hand[index + 1].getNumber() == hand[index].getNumber() + 1)
                        && (hand[index + 2].getType() == hand[index].getType() && hand[index + 2].getNumber() == hand[index].getNumber() + 1)) {
                        var group = mutableListOf<Tile>()
                        group.add(Tile(hand[index].getType(), hand[index].getNumber()))
                        group.add(Tile(hand[index + 1].getType(), hand[index + 1].getNumber()))
                        group.add(Tile(hand[index + 2].getType(), hand[index + 2].getNumber()))
                        groupList.add(group)
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
    //ヤオチュー牌
    TERMINAL_AND_HONOR{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO || (tile.getNumber() == 1 && tile.getNumber() == 9))
                    return true
            }
            return false
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            groupList.clear()
            for(tile in hand){
                if(tile.getType() > Tile.Type.BAMBOO || (tile.getNumber() == 1 && tile.getNumber() == 9))
                    groupList.add(mutableListOf(tile))
            }
            return groupList
        }
    },
    //タイプごとの属性
    TYPE{
        override fun existGroup(hand: MutableList<Tile>): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getGroupList(hand: MutableList<Tile>): MutableList<MutableList<Tile>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    //全ての順番
    STRAIGHT{
        fun containAllNumber(type: Tile.Type, hand: MutableList<Tile>): Boolean{
            for(index in (1..type.getTypeSize())){
                if(hand.find{it.getNumber() == index} == null)
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
                if(containAllNumber(type, hand)) {
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