package com.example.nanshanten

import android.util.Log

class Hand{
    private lateinit var tiles: MutableList<Tile>
    private lateinit var drawTile: Tile
    private lateinit var pungs: MutableList<Tile>
    private lateinit var chows: MutableList<Tile>
    private lateinit var kongs: MutableList<Tile>

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
        tiles.sortWith(compareBy({it.getType()}, {it.getNumber()}))
    }

    fun getHand(): MutableList<Tile>{
        return tiles
    }

    fun getHandWithDraw(): MutableList<Tile>{
        return tiles.toMutableList().plusElement(drawTile).sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
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
        return tiles.toMutableList().plus(chows).plus(pungs).plus(kongs).sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
    }

    fun chow(removeTiles: MutableList<Tile>, discardTile: Tile){
        for(tile in removeTiles) {
            chows.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.getType() != Tile.Type.UNDEFINED)
            chows.add(discardTile)
    }

    fun pung(removeTiles: MutableList<Tile>, discardTile: Tile){
        for(tile in removeTiles) {
            pungs.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.getType() != Tile.Type.UNDEFINED)
            pungs.add(discardTile)
    }

    fun kong(removeTiles: MutableList<Tile>, discardTile: Tile){
        if(drawTile.getType() != Tile.Type.UNDEFINED){
            kongs.add(removeTiles.get(0))
            removeTiles.removeAt(0)
            drawTile = Tile(Tile.Type.UNDEFINED, 0)
        }
        for(tile in removeTiles) {
            kongs.add(tiles.find { it.equals(tile) }!!)
            tiles.remove(tile)
        }
        if(discardTile.getType() != Tile.Type.UNDEFINED)
            kongs.add(discardTile)
    }

    fun getRemoveList(elements: MutableList<Tile>): MutableList<Tile>{
        val removedList = tiles.toMutableList()
        for(tile in elements){
            removedList.remove(tile)
        }
        return removedList.sortedWith(compareBy({it.getType()}, {it.getNumber()})).toMutableList()
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