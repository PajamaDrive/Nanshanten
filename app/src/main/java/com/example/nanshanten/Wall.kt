package com.example.nanshanten

class Wall{
    private lateinit var tiles: MutableList<Tile>
    private lateinit var knownTiles: MutableList<Tile>
    init {
        tiles = mutableListOf()
        for (type in Tile.Type.values()) {
            if (type != Tile.Type.UNDEFINED) {
                for (number in 1..type.getTypeSize())
                    for(it in 0..3)
                        tiles.add(Tile(type, number))
            }
        }
        knownTiles = mutableListOf()
    }

    fun count(tile: Tile): Int{
        return tiles.count { it.equals(tile) }
    }

    fun remove(removeTile: Tile){
        knownTiles.add(removeTile)
        tiles.remove(removeTile)
    }

    fun removeAll(removeList: MutableList<Tile>){
        knownTiles.addAll(removeList)
        removeList.forEach {
            tiles.remove(it)
        }
    }

    fun getWall(): MutableList<Tile>{
        return tiles
    }

    fun getKnownTiles(): MutableList<Tile>{
        return knownTiles
    }
}