package com.example.nanshanten

class Wall{
    private var absoluteTiles: MutableList<Tile>
    private var unknownTiles: MutableList<Tile>
    private var knownTiles: MutableList<Tile>
    init {
        absoluteTiles = mutableListOf()
        for (type in Tile.Type.values()) {
            if (type != Tile.Type.UNDEFINED) {
                for (number in 1..type.getTypeSize())
                    for(it in 0..3)
                        absoluteTiles.add(Tile(type, number))
            }
        }
        unknownTiles = absoluteTiles.toMutableList()
        knownTiles = mutableListOf()
    }

    fun count(tile: Tile): Int{
        return unknownTiles.count { it.equals(tile) }
    }

    fun maxCount(): Int{
        return unknownTiles.map { count(it) }.max()!!
    }

    fun remove(removeTile: Tile){
        knownTiles.add(removeTile)
        unknownTiles.remove(removeTile)
        absoluteTiles.remove(removeTile)
    }

    fun removeAll(removeList: MutableList<Tile>){
        knownTiles.addAll(removeList)
        removeList.forEach {
            unknownTiles.remove(it)
            absoluteTiles.remove(it)
        }
    }

    fun getWall(): MutableList<Tile>{
        return absoluteTiles
    }

    fun getUnknownTiles(): MutableList<Tile>{
        return unknownTiles
    }

    fun getKnownTiles(): MutableList<Tile>{
        return knownTiles
    }
}