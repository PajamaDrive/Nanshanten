package com.example.nanshanten

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class HandFragment : Fragment(R.layout.activity_main){

    val dragListner = View.OnDragListener { v, event ->

        // Handles each of the expected events
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                // Determines if this View can accept the dragged data
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    true
                } else {
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                (v as LinearLayout).setBackgroundResource(R.drawable.border)
                true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                // Ignore the event
                true
            DragEvent.ACTION_DRAG_EXITED -> {
                (v as LinearLayout).background = null

                true
            }
            DragEvent.ACTION_DROP -> {
                // Gets the item containing the dragged data

                changeHand(v, Integer.parseInt(event.clipData.getItemAt(0).text.toString()))

                // Invalidates the view to force a redraw
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                true
            }
            else -> {
                // An unknown action type was received.
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                false
            }
        }
    }

    val longClicListner = View.OnLongClickListener { v: View ->

        v.tag = v.id.toString()
        // Create a new ClipData.Item from the ImageView object's tag
        val item = ClipData.Item(v.tag as? CharSequence)

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        val dragData = ClipData(
            v.tag as? CharSequence,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item)

        // Instantiates the drag shadow builder.
        val myShadow = MyDragShadowBuilder(v)

        // Starts the drag
        v.startDrag(
            dragData,   // the data to be dragged
            myShadow,   // the drag shadow builder
            null,       // no need to use local data
            0           // flags (not currently used, set to 0)
        )
    }

    lateinit var handViewId: Map<Int, Int>
    val hand = Hand()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        initiallize()
    }

    fun changeHand(view: View, id: Int){
        val tileImageId = getDragImageId(id)
        val tileText = Tile.getTileText(Tile.getTileType(id), Tile.getTileNumber(id))
        (view as LinearLayout).background = null
        (view.getChildAt(0) as ImageView).setImageResource(tileImageId)
        (view.getChildAt(1) as TextView).text = tileText
        hand.changeTile(handViewId.get(view.id)!! - 1, Tile(Tile.getTileType(id), Tile.getTileNumber(id)))
        yakuText.text = Yaku.getYakuList(hand).toString()
        Log.d("debug", "change at hand" + handViewId.get(view.id)!!.toString() + ", " + hand.getTile(handViewId.get(view.id)!! - 1).toString())
    }

    fun getDragImageId(dragId: Int): Int{
        return resources.getIdentifier(Tile.getTileIdText(dragId), "drawable", "com.example.nanshanten")
    }

    fun initiallize(){
        Tile.setImageId(tile_character1Image.id, listOf("1", "character1"))
        Tile.setImageId(tile_character2Image.id, listOf("2", "character2"))
        Tile.setImageId(tile_character3Image.id, listOf("3", "character3"))
        Tile.setImageId(tile_character4Image.id, listOf("4", "character4"))
        Tile.setImageId(tile_character5Image.id, listOf("5", "character5"))
        Tile.setImageId(tile_character6Image.id, listOf("6", "character6"))
        Tile.setImageId(tile_character7Image.id, listOf("7", "character7"))
        Tile.setImageId(tile_character8Image.id, listOf("8", "character8"))
        Tile.setImageId(tile_character9Image.id, listOf("9", "character9"))
        Tile.setImageId(tile_circle1Image.id, listOf("1", "circle1"))
        Tile.setImageId(tile_circle2Image.id, listOf("2", "circle2"))
        Tile.setImageId(tile_circle3Image.id, listOf("3", "circle3"))
        Tile.setImageId(tile_circle4Image.id, listOf("4", "circle4"))
        Tile.setImageId(tile_circle5Image.id, listOf("5", "circle5"))
        Tile.setImageId(tile_circle6Image.id, listOf("6", "circle6"))
        Tile.setImageId(tile_circle7Image.id, listOf("7", "circle7"))
        Tile.setImageId(tile_circle8Image.id, listOf("8", "circle8"))
        Tile.setImageId(tile_circle9Image.id, listOf("9", "circle9"))
        Tile.setImageId(tile_bamboo1Image.id, listOf("1", "bamboo1"))
        Tile.setImageId(tile_bamboo2Image.id, listOf("2", "bamboo2"))
        Tile.setImageId(tile_bamboo3Image.id, listOf("3", "bamboo3"))
        Tile.setImageId(tile_bamboo4Image.id, listOf("4", "bamboo4"))
        Tile.setImageId(tile_bamboo5Image.id, listOf("5", "bamboo5"))
        Tile.setImageId(tile_bamboo6Image.id, listOf("6", "bamboo6"))
        Tile.setImageId(tile_bamboo7Image.id, listOf("7", "bamboo7"))
        Tile.setImageId(tile_bamboo8Image.id, listOf("8", "bamboo8"))
        Tile.setImageId(tile_bamboo9Image.id, listOf("9", "bamboo9"))
        Tile.setImageId(tile_eastImage.id, listOf("1", "east"))
        Tile.setImageId(tile_southImage.id, listOf("2", "south"))
        Tile.setImageId(tile_westImage.id, listOf("3", "west"))
        Tile.setImageId(tile_northImage.id, listOf("4", "north"))
        Tile.setImageId(tile_whiteDragonImage.id, listOf("1", "whitedragon"))
        Tile.setImageId(tile_greenDragonImage.id, listOf("2", "greendragon"))
        Tile.setImageId(tile_redDragonImage.id, listOf("3", "reddragon"))

        tile_character1Image.setOnLongClickListener(longClicListner)
        tile_character2Image.setOnLongClickListener(longClicListner)
        tile_character3Image.setOnLongClickListener(longClicListner)
        tile_character4Image.setOnLongClickListener(longClicListner)
        tile_character5Image.setOnLongClickListener(longClicListner)
        tile_character6Image.setOnLongClickListener(longClicListner)
        tile_character7Image.setOnLongClickListener(longClicListner)
        tile_character8Image.setOnLongClickListener(longClicListner)
        tile_character9Image.setOnLongClickListener(longClicListner)
        tile_circle1Image.setOnLongClickListener(longClicListner)
        tile_circle2Image.setOnLongClickListener(longClicListner)
        tile_circle3Image.setOnLongClickListener(longClicListner)
        tile_circle4Image.setOnLongClickListener(longClicListner)
        tile_circle5Image.setOnLongClickListener(longClicListner)
        tile_circle6Image.setOnLongClickListener(longClicListner)
        tile_circle7Image.setOnLongClickListener(longClicListner)
        tile_circle8Image.setOnLongClickListener(longClicListner)
        tile_circle9Image.setOnLongClickListener(longClicListner)
        tile_bamboo1Image.setOnLongClickListener(longClicListner)
        tile_bamboo2Image.setOnLongClickListener(longClicListner)
        tile_bamboo3Image.setOnLongClickListener(longClicListner)
        tile_bamboo4Image.setOnLongClickListener(longClicListner)
        tile_bamboo5Image.setOnLongClickListener(longClicListner)
        tile_bamboo6Image.setOnLongClickListener(longClicListner)
        tile_bamboo7Image.setOnLongClickListener(longClicListner)
        tile_bamboo8Image.setOnLongClickListener(longClicListner)
        tile_bamboo9Image.setOnLongClickListener(longClicListner)
        tile_eastImage.setOnLongClickListener(longClicListner)
        tile_southImage.setOnLongClickListener(longClicListner)
        tile_westImage.setOnLongClickListener(longClicListner)
        tile_northImage.setOnLongClickListener(longClicListner)
        tile_whiteDragonImage.setOnLongClickListener(longClicListner)
        tile_greenDragonImage.setOnLongClickListener(longClicListner)
        tile_redDragonImage.setOnLongClickListener(longClicListner)

        hand1.setOnDragListener(dragListner)
        hand2.setOnDragListener(dragListner)
        hand3.setOnDragListener(dragListner)
        hand4.setOnDragListener(dragListner)
        hand5.setOnDragListener(dragListner)
        hand6.setOnDragListener(dragListner)
        hand7.setOnDragListener(dragListner)
        hand8.setOnDragListener(dragListner)
        hand9.setOnDragListener(dragListner)
        hand10.setOnDragListener(dragListner)
        hand11.setOnDragListener(dragListner)
        hand12.setOnDragListener(dragListner)
        hand13.setOnDragListener(dragListner)
        hand14.setOnDragListener(dragListner)

        handViewId = mapOf(hand1.id to 1, hand2.id to 2, hand3.id to 3, hand4.id to 4, hand5.id to 5, hand6.id to 6, hand7.id to 7,
            hand8.id to 8, hand9.id to 9, hand10.id to 10, hand11.id to 11, hand12.id to 12, hand13.id to 13, hand14.id to 14)

        changeHand(hand1, tile_character1Image.id)
        changeHand(hand2, tile_character9Image.id)
        changeHand(hand3, tile_circle1Image.id)
        changeHand(hand4, tile_circle9Image.id)
        changeHand(hand5, tile_bamboo1Image.id)
        changeHand(hand6, tile_bamboo9Image.id)
        changeHand(hand7, tile_eastImage.id)
        changeHand(hand8, tile_southImage.id)
        changeHand(hand9, tile_westImage.id)
        changeHand(hand10, tile_northImage.id)
        changeHand(hand11, tile_whiteDragonImage.id)
        changeHand(hand12, tile_greenDragonImage.id)
        changeHand(hand13, tile_redDragonImage.id)
        changeHand(hand14, tile_character1Image.id)

    }
}

private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = v

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {

        // Sets the width of the shadow to half the width of the original View
        val width: Int = view.width / 2

        // Sets the height of the shadow to half the height of the original View
        val height: Int = view.height + 50

        size.set(view.width, view.height)
        touch.set(width,height);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}