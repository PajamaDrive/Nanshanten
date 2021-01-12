package com.example.nanshanten

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.right
import android.R.attr.left



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
        val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)

        // Instantiates the drag shadow builder.
        val myShadow = MyDragShadowBuilder(v)

        // Starts the drag
        v.startDrag(dragData, myShadow, null, 0)
    }

    val clickListener = View.OnClickListener { v: View ->
        if(selectedHand.indexOf(handViewId.get(v.id)!! - 1) == -1){
            if(selectedHand.size < 4) {
                selectedHand.add(handViewId.get(v.id)!! - 1)
                (v as LinearLayout).setBackgroundResource(R.drawable.click_border)
            }
        }
        else{
            selectedHand.remove(handViewId.get(v.id)!! - 1)
            (v as LinearLayout).background = null
        }
        canClaim()
    }

    val claimButtonListener = { type: String ->
        View.OnClickListener { v: View ->
            claimArea.visibility = View.VISIBLE
            var layoutContainer = LinearLayout(ContextGetter.applicationContext())
            layoutContainer.orientation = LinearLayout.HORIZONTAL
            val sortedSelected = selectedHand.sortedBy { hand.getHand().get(it).getNumber() }.toMutableList()
            val subHand = hand.getHand().filterIndexed { index, tile -> index == sortedSelected.find { it == index } }.toMutableList()
            for (index in (0..(subHand.size - 1))) {
                val layout = LinearLayout(ContextGetter.applicationContext())
                val imageView = ImageView(ContextGetter.applicationContext())
                val textView = TextView(ContextGetter.applicationContext())
                val handView = activity!!.findViewById<LinearLayout>(handViewId.keys.elementAt(sortedSelected.get(index)))
                val imageId = resources.getIdentifier(Tile.getTileIdTextByText((handView.getChildAt(1) as TextView).text.toString()), "drawable", "com.example.nanshanten")
                layout.orientation = LinearLayout.VERTICAL
                imageView.setImageResource(imageId)
                imageView.layoutParams = LinearLayout.LayoutParams(dpToPx(40),dpToPx(55))
                textView.text = (handView.getChildAt(1) as TextView).text
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f)
                textView.gravity = Gravity.CENTER

                layout.addView(imageView)
                layout.addView(textView)
                val layoutMarginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) as ViewGroup.MarginLayoutParams
                layoutMarginParams.setMargins(dpToPx(1), dpToPx(5), dpToPx(1), dpToPx(0))
                layout.layoutParams = layoutMarginParams
                layoutContainer.addView(layout)
                val containerMarginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) as ViewGroup.MarginLayoutParams
                containerMarginParams.setMargins(dpToPx(5), dpToPx(0), dpToPx(10), dpToPx(0))
                layoutContainer.layoutParams = containerMarginParams
            }
            claimTiles.addView(layoutContainer)
            if (type.equals("pung")) {
                hand.pung(sortedSelected)
            }
            if (type.equals("chow")) {
                hand.chow(sortedSelected)
            }
            if (type.equals("kong")) {
                hand.kong(sortedSelected)
            }
            updateView()
        }
    }

    lateinit var handViewId: Map<Int, Int>
    val hand = Hand()
    val selectedHand = mutableListOf<Int>()

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

    fun changeHand(view: View, id: Int) {
        val tile = Tile(Tile.getTileType(id), Tile.getTileNumber(id))
        (view as LinearLayout).background = null
        if (hand.getHand().count { it.equals(tile) } < 4) {
            val tileImageId = getDragImageId(id)
            val tileText = Tile.getTileText(Tile.getTileType(id), Tile.getTileNumber(id))
            (view.getChildAt(0) as ImageView).setImageResource(tileImageId)
            (view.getChildAt(1) as TextView).text = tileText
            if(selectedHand.indexOf(handViewId.get(view.id)!! - 1) != -1) {
                selectedHand.remove(handViewId.get(view.id)!! - 1)
                canClaim()
            }
            hand.changeTile(handViewId.get(view.id)!! - 1, tile)
            yakuText.text = Yaku.getYakuList(hand).toString()
            Log.d("debug", "change at hand" + handViewId.get(view.id)!!.toString() + ", " + hand.getTile(handViewId.get(view.id)!! - 1).toString())
        }
        updateView()
    }

    fun canClaim(){
        val subHands = hand.getHand().filterIndexed { index, tile -> index == selectedHand.find { it == index } }.toMutableList()
        if (subHands.size == 3 && TileGroup.PUNG.getGroupListNum(subHands) == 1) {
            pungButton.visibility = View.VISIBLE
        } else {
            pungButton.visibility = View.GONE
        }
        if (subHands.size == 3 && TileGroup.CHOW.getGroupListNum(subHands) == 1) {
            chowButton.visibility = View.VISIBLE
        }
        else {
            chowButton.visibility = View.GONE
        }
        if (TileGroup.KONG.getGroupListNum(subHands) == 1) {
            kongButton.visibility = View.VISIBLE
        } else {
            kongButton.visibility = View.GONE
        }
    }

    fun updateView(){
        hand.sortHand()
        for(index in 1..14) {
            val handView = activity!!.findViewById<LinearLayout>(handViewId.keys.elementAt(index - 1))
            if (hand.getHand().size >= index) {
                val imageId = resources.getIdentifier(Tile.getTileIdTextByText(hand.getHand().get(index - 1).toString()), "drawable", "com.example.nanshanten")
                (handView.getChildAt(0) as ImageView).setImageResource(imageId)
                (handView.getChildAt(1) as TextView).text = hand.getHand().get(index - 1).toString()
            }
            else{
                val imageId = resources.getIdentifier("null_tile", "drawable", "com.example.nanshanten")
                (handView.getChildAt(0) as ImageView).setImageResource(imageId)
                (handView.getChildAt(1) as TextView).text = ""
                handView.setOnClickListener(null)
                handView.setOnLongClickListener(null)
                pungButton.visibility = View.GONE
                chowButton.visibility = View.GONE
                kongButton.visibility = View.GONE
            }
            handView.background = null
        }
        selectedHand.clear()
    }

    fun getDragImageId(dragId: Int): Int{
        return resources.getIdentifier(Tile.getTileIdText(dragId), "drawable", "com.example.nanshanten")
    }

    fun dpToPx(dp: Int): Int{
        return Math.round(resources.displayMetrics.density * dp)
    }

    fun initiallize(){
        Tile.setImageId(tile_character1Image.id, listOf("1", "character1", "一萬"))
        Tile.setImageId(tile_character2Image.id, listOf("2", "character2", "二萬"))
        Tile.setImageId(tile_character3Image.id, listOf("3", "character3", "三萬"))
        Tile.setImageId(tile_character4Image.id, listOf("4", "character4", "四萬"))
        Tile.setImageId(tile_character5Image.id, listOf("5", "character5", "五萬"))
        Tile.setImageId(tile_character6Image.id, listOf("6", "character6", "六萬"))
        Tile.setImageId(tile_character7Image.id, listOf("7", "character7", "七萬"))
        Tile.setImageId(tile_character8Image.id, listOf("8", "character8", "八萬"))
        Tile.setImageId(tile_character9Image.id, listOf("9", "character9", "九萬"))
        Tile.setImageId(tile_circle1Image.id, listOf("1", "circle1", "一筒"))
        Tile.setImageId(tile_circle2Image.id, listOf("2", "circle2", "二筒"))
        Tile.setImageId(tile_circle3Image.id, listOf("3", "circle3", "三筒"))
        Tile.setImageId(tile_circle4Image.id, listOf("4", "circle4", "四筒"))
        Tile.setImageId(tile_circle5Image.id, listOf("5", "circle5", "五筒"))
        Tile.setImageId(tile_circle6Image.id, listOf("6", "circle6", "六筒"))
        Tile.setImageId(tile_circle7Image.id, listOf("7", "circle7", "七筒"))
        Tile.setImageId(tile_circle8Image.id, listOf("8", "circle8", "八筒"))
        Tile.setImageId(tile_circle9Image.id, listOf("9", "circle9", "九筒"))
        Tile.setImageId(tile_bamboo1Image.id, listOf("1", "bamboo1", "一索"))
        Tile.setImageId(tile_bamboo2Image.id, listOf("2", "bamboo2", "二索"))
        Tile.setImageId(tile_bamboo3Image.id, listOf("3", "bamboo3", "三索"))
        Tile.setImageId(tile_bamboo4Image.id, listOf("4", "bamboo4", "四索"))
        Tile.setImageId(tile_bamboo5Image.id, listOf("5", "bamboo5", "五索"))
        Tile.setImageId(tile_bamboo6Image.id, listOf("6", "bamboo6", "六索"))
        Tile.setImageId(tile_bamboo7Image.id, listOf("7", "bamboo7", "七索"))
        Tile.setImageId(tile_bamboo8Image.id, listOf("8", "bamboo8", "八索"))
        Tile.setImageId(tile_bamboo9Image.id, listOf("9", "bamboo9", "九索"))
        Tile.setImageId(tile_eastImage.id, listOf("1", "east", "東"))
        Tile.setImageId(tile_southImage.id, listOf("2", "south", "南"))
        Tile.setImageId(tile_westImage.id, listOf("3", "west", "西"))
        Tile.setImageId(tile_northImage.id, listOf("4", "north", "北"))
        Tile.setImageId(tile_whiteDragonImage.id, listOf("1", "whitedragon", "白"))
        Tile.setImageId(tile_greenDragonImage.id, listOf("2", "greendragon", "發"))
        Tile.setImageId(tile_redDragonImage.id, listOf("3", "reddragon", "中"))

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
        hand1.setOnClickListener(clickListener)
        hand2.setOnDragListener(dragListner)
        hand2.setOnClickListener(clickListener)
        hand3.setOnDragListener(dragListner)
        hand3.setOnClickListener(clickListener)
        hand4.setOnDragListener(dragListner)
        hand4.setOnClickListener(clickListener)
        hand5.setOnDragListener(dragListner)
        hand5.setOnClickListener(clickListener)
        hand6.setOnDragListener(dragListner)
        hand6.setOnClickListener(clickListener)
        hand7.setOnDragListener(dragListner)
        hand7.setOnClickListener(clickListener)
        hand8.setOnDragListener(dragListner)
        hand8.setOnClickListener(clickListener)
        hand9.setOnDragListener(dragListner)
        hand9.setOnClickListener(clickListener)
        hand10.setOnDragListener(dragListner)
        hand10.setOnClickListener(clickListener)
        hand11.setOnDragListener(dragListner)
        hand11.setOnClickListener(clickListener)
        hand12.setOnDragListener(dragListner)
        hand12.setOnClickListener(clickListener)
        hand13.setOnDragListener(dragListner)
        hand13.setOnClickListener(clickListener)
        hand14.setOnDragListener(dragListner)
        hand14.setOnClickListener(clickListener)

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


        pungButton.setOnClickListener(claimButtonListener("pung"))
        chowButton.setOnClickListener(claimButtonListener("chow"))
        kongButton.setOnClickListener(claimButtonListener("kong"))
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