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

    fun getHandWithoutNull(): MutableList<Tile>{
        return tiles.filter{ !it.equals(Tile(Tile.Type.UNDEFINED, 0)) }.toMutableList()
    }

    fun pushHand(tile: Tile){
        tiles.add(tile)
    }

    fun getLast(): Tile{
        return tiles.last()
    }

    fun popHand(): Tile{
        return tiles.removeAt(tiles.size - 1)
    }
}