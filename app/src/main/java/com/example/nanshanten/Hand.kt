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

    enum class TileGroup {
        CHOW{
            override fun existTileGroup(): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupList(): List<List<Tile>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupListNum(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        },

        PUNG{
            override fun existTileGroup(): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupList(): List<List<Tile>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupListNum(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        },

        KONG{
            override fun existTileGroup(): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupList(): List<List<Tile>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupListNum(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        },

        PAIR{
            override fun existTileGroup(): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupList(): List<List<Tile>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getTileGroupListNum(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        };

        abstract fun existTileGroup(): Boolean
        abstract fun getTileGroupList(): List<List<Tile>>
        abstract fun getTileGroupListNum(): Int

    }
}