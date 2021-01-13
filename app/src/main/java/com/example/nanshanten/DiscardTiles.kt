package com.example.nanshanten

class DiscardTiles {
    private lateinit var tiles: MutableList<Tile>

    init{
        tiles = mutableListOf<Tile>()
    }

    fun getSortedHand(): MutableList<Tile>{
        return tiles.sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
    }

    fun getHand(): MutableList<Tile>{
        return tiles
    }

    fun pushHand(tile: Tile){
        tiles.add(tile)
    }
}