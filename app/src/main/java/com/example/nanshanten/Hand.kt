package com.example.nanshanten

import android.util.Log

class Hand{
    private lateinit var tiles: MutableList<Tile>
    private lateinit var drawTile: Tile
    lateinit var pungs: MutableList<MutableList<Tile>>
    lateinit var chows: MutableList<MutableList<Tile>>
    lateinit var kongs: MutableList<MutableList<Tile>>
    var claimed = false

    init{
        tiles = mutableListOf(Tile(Tile.Type.CHARACTER, 1), Tile(Tile.Type.CHARACTER, 9),
            Tile(Tile.Type.CIRCLE, 1), Tile(Tile.Type.CIRCLE, 9),
            Tile(Tile.Type.BAMBOO, 1), Tile(Tile.Type.BAMBOO, 9),
            Tile(Tile.Type.WIND, 1), Tile(Tile.Type.WIND, 2), Tile(Tile.Type.WIND, 3),
            Tile(Tile.Type.WIND, 4), Tile(Tile.Type.DRAGON, 1), Tile(Tile.Type.DRAGON, 2),
            Tile(Tile.Type.DRAGON, 3))
        drawTile = Tile(Tile.Type.UNDEFINED, 0)
        pungs = mutableListOf()
        chows = mutableListOf()
        kongs = mutableListOf()
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

    fun sortHand(){
        tiles.sortWith(compareBy({it.type}, {it.number}))
    }

    fun getHand(): MutableList<Tile>{
        return tiles
    }

    fun getHandWithDraw(): MutableList<Tile>{
        return tiles.toMutableList().plusElement(drawTile).sortedWith(compareBy({it.type}, {it.number})).toMutableList()
    }

    fun setDraw(tile: Tile){
        drawTile = tile
    }

    fun discardTile(index: Int){
        if(index != 13){
            tiles.removeAt(index)
            tiles.add(drawTile!!)
        }
        drawTile = Tile(Tile.Type.UNDEFINED, 0)
    }

    fun addDrawToHand(){
        tiles.add(drawTile!!)
        drawTile = Tile(Tile.Type.UNDEFINED, 0)
    }

    fun getDraw(): Tile{
        return drawTile
    }

    fun getSortAllHand(): MutableList<Tile>{
        return tiles.toMutableList().plus(chows.flatten()).plus(pungs.flatten()).plus(kongs.flatten()).sortedWith(compareBy({it.type}, {it.number})).toMutableList()
    }

    fun chow(removeTiles: MutableList<Tile>, discardTile: Tile){
        claimed = true
        val addList = mutableListOf<Tile>()
        for(tile in removeTiles) {
            addList.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.type != Tile.Type.UNDEFINED)
            addList.add(discardTile)
        chows.add(addList)
    }

    fun pung(removeTiles: MutableList<Tile>, discardTile: Tile){
        claimed = true
        val addList = mutableListOf<Tile>()
        for(tile in removeTiles) {
            addList.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.type != Tile.Type.UNDEFINED)
            addList.add(discardTile)
        pungs.add(addList)
    }

    fun kong(removeTiles: MutableList<Tile>, discardTile: Tile){
        val addList = mutableListOf<Tile>()
        if(drawTile.type != Tile.Type.UNDEFINED){
            addList.add(removeTiles.get(0))
            removeTiles.removeAt(0)
            drawTile = Tile(Tile.Type.UNDEFINED, 0)
        } else{
            claimed = true
        }
        for(tile in removeTiles) {
            addList.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.type != Tile.Type.UNDEFINED)
            addList.add(discardTile)
        kongs.add(addList)
    }

    fun extendKong(tile: Tile){
        pungs.remove(pungs.find { it.get(0).equals(tile) })
        kongs.add(mutableListOf(tile, tile, tile, tile))
        addDrawToHand()
        tiles.remove(tile)
    }

    fun getRemoveList(elements: MutableList<Tile>): MutableList<Tile>{
        val removedList = tiles.toMutableList()
        for(tile in elements){
            removedList.remove(tile)
        }
        return removedList.sortedWith(compareBy({it.type}, {it.number})).toMutableList()
    }

    fun canRemove(elements: MutableList<Tile>): Boolean{
        val removedList = tiles.toMutableList()
        for(tile in elements){
            if(removedList.indexOf(tile) == -1)
                return false
            else
                removedList.remove(tile)
        }
        return true
    }
}