package com.example.nanshanten

import kotlin.reflect.typeOf

class Tile(val type: Type, val number: Int){

    companion object{
        private val circleTextMap = mapOf(1 to "一筒", 2 to "二筒", 3 to "三筒", 4 to "四筒", 5 to "五筒", 6 to "六筒", 7 to "七筒",
            8 to "八筒", 9 to "九筒")
        private val bambooTextMap = mapOf(1 to "一索", 2 to "二索", 3 to "三索", 4 to "四索", 5 to "五索", 6 to "六索", 7 to "七索",
            8 to "八索", 9 to "九索")
        private val characterTextMap = mapOf(1 to "一萬", 2 to "二萬", 3 to "三萬", 4 to "四萬", 5 to "五萬", 6 to "六萬", 7 to "七萬",
            8 to "八萬", 9 to "九萬")
        private val windTextMap = mapOf(1 to "東", 2 to "南", 3 to "西", 4 to "北")
        private val windTitleMap = mapOf(1 to "east", 2 to "south", 3 to "west", 4 to "north")
        private val dragonTextMap = mapOf(1 to "白", 2 to "發", 3 to "中")
        private val dragonTitleMap = mapOf(1 to "whitedragon", 2 to "greendragon", 3 to "reddragon")
        private val imageIdMap = mutableMapOf<Int, List<String>>()

        fun getTileText(type: Type, num: Int): String{
            return when(type){
                Type.CHARACTER -> { characterTextMap.get(num)!!}
                Type.CIRCLE-> { circleTextMap.get(num)!!}
                Type.BAMBOO -> { bambooTextMap.get(num)!!}
                Type.WIND -> { windTextMap.get(num)!!}
                Type.DRAGON -> { dragonTextMap.get(num)!!}
                else -> { "" }
            }
        }

        fun setImageId(id: Int, data: List<String>){
            imageIdMap.set(id, data)
        }

        fun getTileType(id: Int): Type{
            return when{
                Regex("character").containsMatchIn(imageIdMap.get(id)!!.get(1)) -> {Type.CHARACTER}
                Regex("circle").containsMatchIn(imageIdMap.get(id)!!.get(1)) -> {Type.CIRCLE}
                Regex("bamboo").containsMatchIn(imageIdMap.get(id)!!.get(1)) -> {Type.BAMBOO}
                Regex("east|south|west|north").containsMatchIn(imageIdMap.get(id)!!.get(1)) -> {Type.WIND}
                Regex("whitedragon|greendragon|reddragon").containsMatchIn(imageIdMap.get(id)!!.get(1)) -> {Type.DRAGON}
                else -> {Type.UNDEFINED}
            }
        }

        fun getTileNumber(id: Int): Int{
            return Integer.parseInt(imageIdMap.get(id)!!.get(0))
        }

        fun getTileIdText(id: Int): String{
            return imageIdMap.get(id)!!.get(1)
        }

        fun getTileIdTextByText(text: String): String{
            return imageIdMap.values.toList().find { it.get(2).equals(text) }!!.get(1)
        }

        fun getTileTypeByText(text: String): Type{
            return when{
                Regex("萬").containsMatchIn(text) -> {Type.CHARACTER}
                Regex("筒").containsMatchIn(text) -> {Type.CIRCLE}
                Regex("索").containsMatchIn(text) -> {Type.BAMBOO}
                Regex("東|南|西|北").containsMatchIn(text) -> {Type.WIND}
                Regex("白|發|中").containsMatchIn(text) -> {Type.DRAGON}
                else -> {Type.UNDEFINED}
            }
        }

        fun getTileNumberByText(text: String): Int{
            return when{
                Regex("一|東|白").containsMatchIn(text) -> 1
                Regex("二|南|發").containsMatchIn(text) -> 2
                Regex("三|西|中").containsMatchIn(text) -> 3
                Regex("四|北").containsMatchIn(text) -> 4
                Regex("五").containsMatchIn(text) -> 5
                Regex("六").containsMatchIn(text) -> 6
                Regex("七").containsMatchIn(text) -> 7
                Regex("八").containsMatchIn(text) -> 8
                Regex("九").containsMatchIn(text) -> 9
                else -> 0
            }
        }
    }

    private lateinit var text: String
    private lateinit var imageTitle: String

    init{
        setTile(number)
    }

    fun setTile(num:Int){
        when(type){
            Type.CHARACTER -> { text = characterTextMap.get(num)!!; imageTitle = "character" + num.toString() }
            Type.CIRCLE -> { text = circleTextMap.get(num)!!; imageTitle = "circle" + num.toString() }
            Type.BAMBOO -> { text = bambooTextMap.get(num)!!; imageTitle = "bamboo" + num.toString() }
            Type.WIND -> { text = windTextMap.get(num)!!; imageTitle = windTitleMap.get(num)!! }
            Type.DRAGON -> { text = dragonTextMap.get(num)!!; imageTitle = dragonTitleMap.get(num)!! }
            else -> { text = ""; imageTitle = "null_tile"}
        }
    }


    override fun toString(): String {
        return text
    }

    override fun equals(other: Any?): Boolean {
        if(other is Tile){
            if(other.type == type && other.number == number)
                return true
        }
        return false
    }

    enum class Type{
        CHARACTER{
            override fun getTypeSize(): Int{
                return 9
            }
        },
        CIRCLE{
            override fun getTypeSize(): Int{
                return 9
            }
        },
        BAMBOO{
            override fun getTypeSize(): Int{
                return 9
            }
        },
        WIND{
            override fun getTypeSize(): Int{
                return 4
            }
        },
        DRAGON{
            override fun getTypeSize(): Int{
                return 3
            }
        },
        UNDEFINED{
            override fun getTypeSize(): Int{
                return -1
            }
        };
        abstract  fun getTypeSize(): Int
    }
}