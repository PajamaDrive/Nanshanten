package com.example.nanshanten

import android.content.ClipData
import android.content.ClipDescription
import android.drm.DrmStore
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text


class HandFragment : Fragment(R.layout.activity_main){

    val dragListener = View.OnDragListener { v, event ->
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
            DragEvent.ACTION_DRAG_EXITED -> {
                (v as LinearLayout).background = null
                true
            }
            DragEvent.ACTION_DROP -> {
                discardArea.background = null
                if (hand.getDraw().getType() != Tile.Type.UNDEFINED) {
                    discardTile(v, Integer.parseInt(event.clipData.getItemAt(0).text.toString()))
                    changePlayer()
                }
                v.invalidate()
                true
            }
            else -> {
                false
            }
        }
    }

    val longClickListener = View.OnLongClickListener { v: View ->

        if(!(((v as LinearLayout).getChildAt(1) as TextView).text.toString().equals(""))) {
            v.tag = v.id.toString()
            // Create a new ClipData.Item from the ImageView object's tag
            val item = ClipData.Item(v.tag as? CharSequence)

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            val dragData =
                ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)

            // Instantiates the drag shadow builder.
            val myShadow = MyDragShadowBuilder(v)
            // Starts the drag
            v.startDrag(dragData, myShadow, null, 0)
        }else{
            false
        }
    }

    /*
    val handClickListener = View.OnClickListener { v: View ->
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
    */

    val tileClickListener = View.OnClickListener { v: View ->
        changeHand(handNumToLayout.get(14 + currentPlayer)!!, v.id)
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
                val handView = handNumToLayout.get(sortedSelected.get(index) + 1)!!
                val imageId = resources.getIdentifier(Tile.getTileIdTextByText((handView.getChildAt(1) as TextView).text.toString()), "drawable", "com.example.nanshanten")
                layout.orientation = LinearLayout.VERTICAL
                imageView.setImageResource(imageId)
                imageView.layoutParams = LinearLayout.LayoutParams(dpToPx(40),dpToPx(55))
                textView.text = (handView.getChildAt(1) as TextView).text
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
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

    val opponentClaimButtonListener = { type: String, player: Int ->
        View.OnClickListener { v: View ->
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(0) as ImageView).setImageResource(R.drawable.null_tile)
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(1) as TextView).text = ""
            if (type.equals("pung") || type.equals("chow")) {
                changePlayer(player)
            }
            if (type.equals("kong")) {
                changePlayer((player + 1) % 4)
            }
            changeClaimButtonVisivility(View.GONE)

        }
    }

    val confirmButtonListener = View.OnClickListener { v: View ->
        if(!(handNumToLayout.get(14 + currentPlayer)!!.getChildAt(1) as TextView).text.toString().equals("")) {
            discardTile(discardArea, handNumToLayout.get(14 + currentPlayer)!!.id)
            changePlayer()
        }
    }

    lateinit var handIdToNum: Map<Int, Int>
    lateinit var handNumToLayout: MutableMap<Int, LinearLayout>
    val hand = Hand()
    val wall = Wall()
    //0:自分，1:下家，2:対面，3:上家
    val discards = mutableListOf(DiscardTiles(), DiscardTiles(), DiscardTiles(), DiscardTiles())
    var currentPlayer = 0
    lateinit var discardView: Map<Int, LinearLayout>
    val selectedHand = mutableListOf<Int>()
    val spinnerItem = arrayOf("東", "南", "西", "北")

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
        if (wall.count(tile) != 0) {
            val tileImageId = getDragImageId(id)
            val tileText = Tile.getTileText(Tile.getTileType(id), Tile.getTileNumber(id))
            (view.getChildAt(0) as ImageView).setImageResource(tileImageId)
            (view.getChildAt(1) as TextView).text = tileText
            if(selectedHand.indexOf(handIdToNum.get(view.id)!! - 1) != -1) {
                selectedHand.remove(handIdToNum.get(view.id)!! - 1)
                canClaim()
            }
            if(handIdToNum.get(view.id)!! <= hand.getHand().size) {
                hand.changeTile(handIdToNum.get(view.id)!! - 1, tile)
                Log.d("debug", "change at hand" + handIdToNum.get(view.id)!!.toString() + ", " + hand.getTile(handIdToNum.get(view.id)!! - 1).toString())
            }
            else if(handIdToNum.get(view.id)!! == 14) {

                hand.setDraw(tile)
                Log.d("debug", "change at draw, " + hand.getDraw().toString())
            }
            //yakuText.text = Yaku.getYakuList(hand).toString()
        }
        updateView()
    }


    fun discardTile(view: View, id: Int){
        view.background = null

        if(((discardView.get(currentPlayer)!!.getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text.toString().equals(""))
            discardView.get(currentPlayer)!!.removeViewAt(0)

        val layout = LinearLayout(ContextGetter.applicationContext())
        val imageView = ImageView(ContextGetter.applicationContext())
        val textView = TextView(ContextGetter.applicationContext())
        val tileView = activity!!.findViewById<LinearLayout>(id)
        val imageId = resources.getIdentifier(Tile.getTileIdTextByText((tileView.getChildAt(1) as TextView).text.toString()), "drawable", "com.example.nanshanten")
        val tileText = (tileView.getChildAt(1) as TextView).text.toString()
        layout.orientation = LinearLayout.VERTICAL
        imageView.setImageResource(imageId)
        imageView.layoutParams = LinearLayout.LayoutParams(dpToPx(20),dpToPx(27))
        textView.text = tileText
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8.0f)
        textView.gravity = Gravity.CENTER

        layout.addView(imageView)
        layout.addView(textView)

        val layoutMarginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) as ViewGroup.MarginLayoutParams
        layoutMarginParams.setMargins(dpToPx(1), dpToPx(0), dpToPx(1), dpToPx(0))
        layout.layoutParams = layoutMarginParams
        discardView.get(currentPlayer)!!.addView(layout)

        if(currentPlayer == 0) {
            wall.remove(hand.getDraw())
            if (handIdToNum.get(id)!! == 14) {
                discards.get(0).pushHand(hand.getDraw())
            } else {
                discards.get(0).pushHand(hand.getTile(handIdToNum.get(id)!! - 1))
            }
            hand.discardTile(handIdToNum.get(id)!! - 1)
        }
        else{
            val tile = Tile(Tile.getTileTypeByText(tileText), Tile.getTileNumberByText(tileText))
            wall.remove(tile)
            discards.get(currentPlayer).pushHand(tile)
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(0) as ImageView).setImageResource(R.drawable.null_tile)
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(1) as TextView).text = ""
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

    fun changePlayer(num: Int = 0){
        (handNumToLayout.get(14 + currentPlayer)!!.parent as LinearLayout).background = null
        if(num == 0){
            currentPlayer = (currentPlayer + 1) % 4
        }
        else {
            currentPlayer = num
        }
        (handNumToLayout.get(14 + currentPlayer)!!.parent as LinearLayout).setBackgroundResource(R.drawable.border)
        if(currentPlayer == 0) {
            confirmButton.visibility = View.GONE
        }
        else {
            confirmButton.visibility = View.VISIBLE
        }
        changeClaimButtonVisivility(View.VISIBLE)
        changeClaimButtonVisivility(View.GONE, (currentPlayer + 3) % 4)
        Log.d("current", currentPlayer.toString())
    }

    fun changeClaimButtonVisivility(param: Int, player: Int = -1){
        if(player == -1) {
            rightPungButton.visibility = param
            rightKongButton.visibility = param
            oppositePungButton.visibility = param
            oppositeKongButton.visibility = param
            leftPungButton.visibility = param
            leftKongButton.visibility = param
        }
        else{
            when(player){
                1 ->{
                    rightPungButton.visibility = View.GONE
                    rightKongButton.visibility = View.GONE
                }
                2 ->{
                    oppositePungButton.visibility = View.GONE
                    oppositeKongButton.visibility = View.GONE
                }
                3 ->{
                    leftPungButton.visibility = View.GONE
                    leftKongButton.visibility = View.GONE
                }
                else -> {}
            }
        }
    }

    fun updateView(){
        hand.sortHand()
        for(index in 1..14) {
            val handView = handNumToLayout.get(index)!!
            if(index != 14){
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
            }else {
                val imageId = resources.getIdentifier(Tile.getTileIdTextByText(hand.getDraw().toString()), "drawable", "com.example.nanshanten")
                (handView.getChildAt(0) as ImageView).setImageResource(imageId)
                (handView.getChildAt(1) as TextView).text = hand.getDraw().toString()
            }
            handView.background = null
        }
        selectedHand.clear()
        Log.d("wall", wall.getWall().toString())
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
        Tile.setImageId(nullTile.id, listOf("0", "null_tile", ""))

        tile_character1Image.setOnClickListener(tileClickListener)
        tile_character2Image.setOnClickListener(tileClickListener)
        tile_character3Image.setOnClickListener(tileClickListener)
        tile_character4Image.setOnClickListener(tileClickListener)
        tile_character5Image.setOnClickListener(tileClickListener)
        tile_character6Image.setOnClickListener(tileClickListener)
        tile_character7Image.setOnClickListener(tileClickListener)
        tile_character8Image.setOnClickListener(tileClickListener)
        tile_character9Image.setOnClickListener(tileClickListener)
        tile_circle1Image.setOnClickListener(tileClickListener)
        tile_circle2Image.setOnClickListener(tileClickListener)
        tile_circle3Image.setOnClickListener(tileClickListener)
        tile_circle4Image.setOnClickListener(tileClickListener)
        tile_circle5Image.setOnClickListener(tileClickListener)
        tile_circle6Image.setOnClickListener(tileClickListener)
        tile_circle7Image.setOnClickListener(tileClickListener)
        tile_circle8Image.setOnClickListener(tileClickListener)
        tile_circle9Image.setOnClickListener(tileClickListener)
        tile_bamboo1Image.setOnClickListener(tileClickListener)
        tile_bamboo2Image.setOnClickListener(tileClickListener)
        tile_bamboo3Image.setOnClickListener(tileClickListener)
        tile_bamboo4Image.setOnClickListener(tileClickListener)
        tile_bamboo5Image.setOnClickListener(tileClickListener)
        tile_bamboo6Image.setOnClickListener(tileClickListener)
        tile_bamboo7Image.setOnClickListener(tileClickListener)
        tile_bamboo8Image.setOnClickListener(tileClickListener)
        tile_bamboo9Image.setOnClickListener(tileClickListener)
        tile_eastImage.setOnClickListener(tileClickListener)
        tile_southImage.setOnClickListener(tileClickListener)
        tile_westImage.setOnClickListener(tileClickListener)
        tile_northImage.setOnClickListener(tileClickListener)
        tile_whiteDragonImage.setOnClickListener(tileClickListener)
        tile_greenDragonImage.setOnClickListener(tileClickListener)
        tile_redDragonImage.setOnClickListener(tileClickListener)

        hand1.setOnLongClickListener(longClickListener)
        hand2.setOnLongClickListener(longClickListener)
        hand3.setOnLongClickListener(longClickListener)
        hand4.setOnLongClickListener(longClickListener)
        hand5.setOnLongClickListener(longClickListener)
        hand6.setOnLongClickListener(longClickListener)
        hand7.setOnLongClickListener(longClickListener)
        hand8.setOnLongClickListener(longClickListener)
        hand9.setOnLongClickListener(longClickListener)
        hand10.setOnLongClickListener(longClickListener)
        hand11.setOnLongClickListener(longClickListener)
        hand12.setOnLongClickListener(longClickListener)
        hand13.setOnLongClickListener(longClickListener)
        handDraw.setOnLongClickListener(longClickListener)

        /*
        hand1.setOnClickListener(handClickListener)
        hand2.setOnClickListener(handClickListener)
        hand3.setOnClickListener(handClickListener)
        hand4.setOnClickListener(handClickListener)
        hand5.setOnClickListener(handClickListener)
        hand6.setOnClickListener(handClickListener)
        hand7.setOnClickListener(handClickListener)
        hand8.setOnClickListener(handClickListener)
        hand9.setOnClickListener(handClickListener)
        hand10.setOnClickListener(handClickListener)
        hand11.setOnClickListener(handClickListener)
        hand12.setOnClickListener(handClickListener)
        hand13.setOnClickListener(handClickListener)

         */


        pungButton.setOnClickListener(claimButtonListener("pung"))
        chowButton.setOnClickListener(claimButtonListener("chow"))
        kongButton.setOnClickListener(claimButtonListener("kong"))
        discardArea.setOnDragListener(dragListener)
        confirmButton.setOnClickListener(confirmButtonListener)
        rightPungButton.setOnClickListener(opponentClaimButtonListener("pung", 1))
        rightKongButton.setOnClickListener(opponentClaimButtonListener("kong", 1))
        oppositePungButton.setOnClickListener(opponentClaimButtonListener("pung", 2))
        oppositeKongButton.setOnClickListener(opponentClaimButtonListener("kong", 2))
        leftPungButton.setOnClickListener(opponentClaimButtonListener("pung", 3))
        leftKongButton.setOnClickListener(opponentClaimButtonListener("kong", 3))
        changeClaimButtonVisivility(View.GONE)


        handIdToNum = mapOf(hand1.id to 1, hand2.id to 2, hand3.id to 3, hand4.id to 4, hand5.id to 5, hand6.id to 6, hand7.id to 7,
            hand8.id to 8, hand9.id to 9, hand10.id to 10, hand11.id to 11, hand12.id to 12, hand13.id to 13, handDraw.id to 14,
        discardRightPlayer.id to 15, discardOppositePlayer.id to 16, discardLeftPlayer.id to 17)

        handNumToLayout = mutableMapOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handIdToNum.forEach { id, num -> handNumToLayout.set(num, activity!!.findViewById(id)) }
        }

        discardView = mapOf(0 to discardPlayerTiles, 1 to discardRightTiles, 2 to discardOppositeTiles, 3 to discardLeftTiles)

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

        wall.removeAll(hand.getHand())

        val roundAdapter = ArrayAdapter(ContextGetter.applicationContext(), android.R.layout.simple_spinner_item, spinnerItem)
        roundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        round.adapter = roundAdapter
        round.setSelection(0)

        val windAdapter = ArrayAdapter(ContextGetter.applicationContext(), android.R.layout.simple_spinner_item, spinnerItem)
        windAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wind.adapter = windAdapter
        wind.setSelection(0)
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
        val height: Int = view.height + 10

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