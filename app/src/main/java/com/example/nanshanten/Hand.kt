package com.example.nanshanten

class Hand{
    private lateinit var tiles: MutableList<Tile>
    init{
        tiles = mutableListOf(Tile(Tile.Type.CHARACTER, 1), Tile(Tile.Type.CHARACTER, 9),
            Tile(Tile.Type.CIRCLE, 1), Tile(Tile.Type.CIRCLE, 9),
            Tile(Tile.Type.BAMBOO, 1), Tile(Tile.Type.BAMBOO, 9),
            Tile(Tile.Type.WIND, 1), Tile(Tile.Type.WIND, 2), Tile(Tile.Type.WIND, 3),
            Tile(Tile.Type.WIND, 4), Tile(Tile.Type.DRAGON, 1), Tile(Tile.Type.DRAGON, 2),
            Tile(Tile.Type.DRAGON, 3), Tile(Tile.Type.CIRCLE, 1))
    }

    fun changeTile(position: Int, tile: Tile){
        tiles.set(position, tile)
    }

    fun getTile(position: Int): Tile{
        return tiles.get(position)
    }

    fun existTile(tile: Tile): Boolean{
        return tiles.contains(tile)
    }

    fun getSortHand(): MutableList<Tile>{
        return tiles.sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
    }

    fun getRemoveList(elements: MutableList<Tile>): MutableList<Tile>{
        val removedList = getSortHand()
        for(tile in elements){
            removedList.remove(tile)
        }
        return removedList.sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
    }
}