package com.example.nanshanten

import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class HandFragment : Fragment(R.layout.activity_main), ClaimDialogFragment.ClaimDialogListener{
    val tileClickListener = View.OnClickListener { v: View ->
        changeHand(handNumToLayout.get(14 + currentPlayer)!!, ((v as LinearLayout).getChildAt(1) as ImageView).id)
    }

    val handClickListener = View.OnClickListener { v: View ->
        if(!((v as LinearLayout).getChildAt(1) as TextView).text.toString().equals("")){
            if(touchHand.id != (v as LinearLayout).id){
                changeSelectedHand(v, 20)
                if(touchHand != nullTile)
                    changeSelectedHand(touchHand, -20)
                touchHand = v
            }
            else{
                changeSelectedHand(v, -20)
                discardTile(v.id)
                changePlayer()
                touchHand = nullTile
                enableHandListener(false)
                enableTileListener(true)
            }
        }
    }

    val claimButtonListener = { type: String ->
        View.OnClickListener { v: View ->
            execClaim(type, 0)
        }
    }

    val opponentClaimButtonListener = { type: String, player: Int ->
        View.OnClickListener { v: View ->
            execClaim(type, player)
        }
    }

    val confirmButtonListener = View.OnClickListener { v: View ->
        if(!isKong){
            if(currentPlayer == 0){
                wall.remove(hand.getDraw())
                (tileStringToLayout.get(Tile.getTileIdTextByText(hand.getDraw().toString()))!!.getChildAt(0) as TextView).text = "あと" + wall.count(hand.getDraw()) + "枚"
                if(wall.count(hand.getDraw()) == 0)
                    disableTile(tileStringToLayout.get(Tile.getTileIdTextByText(hand.getDraw().toString()))!!)
                if(hand.getDraw().getType() != Tile.Type.UNDEFINED)
                    forceDiscard()
            } else {
                discardTile(handNumToLayout.get(14 + currentPlayer)!!.id)
                changePlayer()
                if(currentPlayer == 0){
                    forceDraw()
                }
            }
        } else {
            changePlayer()
            wall.remove(hand.getDraw())
            (tileStringToLayout.get(Tile.getTileIdTextByText(hand.getDraw().toString()))!!.getChildAt(0) as TextView).text = "あと" + wall.count(hand.getDraw()) + "枚"
            hand.addDrawToHand()
            isKong = false
            updateView()
        }
    }

    lateinit var handIdToNum: Map<Int, Int>
    lateinit var handNumToLayout: MutableMap<Int, LinearLayout>
    lateinit var tileStringToLayout: Map<String, LinearLayout>
    lateinit var claimLayout: Map<Int, LinearLayout>
    val hand = Hand()
    val wall = Wall()
    //0:自分，1:下家，2:対面，3:上家
    val discards = mutableListOf(DiscardTiles(), DiscardTiles(), DiscardTiles(), DiscardTiles())
    var currentPlayer = 0
    lateinit var discardView: Map<Int, LinearLayout>
    val spinnerItem = arrayOf("東", "南", "西", "北")
    val operationTextMap = mapOf(0 to "あなたのツモ牌を選択してください", 1 to "下家の捨て牌を選択してください", 2 to "対面の捨て牌を選択してください", 3 to "上家の捨て牌を選択してください")
    val operationOrClaimTextMap = mapOf(0 to "下家の捨て牌を選択 or 鳴きを選択してください", 1 to "対面の捨て牌を選択 or 鳴きを選択してください", 2 to "上家の捨て牌を選択 or 鳴きを選択してください", 3 to "あなたのツモ牌を選択 or 鳴きを選択してください")
    lateinit var touchHand: LinearLayout
    var isKong = false
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

    override fun onDialogPositiveClick(dialog: ClaimDialogFragment, claimPlayer: Int) {
        createClaimView("chow", claimPlayer, dialog.getSelecteChow(), false, true)
    }

    override fun onDialogNegativeClick(dialog: ClaimDialogFragment, claimPlayer: Int) {
    }

    fun changeHand(view: View, id: Int) {
        val tile = Tile(Tile.getTileType(id), Tile.getTileNumber(id))
        (view as LinearLayout).background = null
        if (wall.count(tile) != 0) {
            val tileImageId = getDragImageId(id)
            val tileText = Tile.getTileText(Tile.getTileType(id), Tile.getTileNumber(id))
            (view.getChildAt(0) as ImageView).setImageResource(tileImageId)
            (view.getChildAt(1) as TextView).text = tileText
            if(handIdToNum.get(view.id)!! <= hand.getHand().size) {
                hand.changeTile(handIdToNum.get(view.id)!! - 1, tile)
                Log.d("debug", "change at hand" + handIdToNum.get(view.id)!!.toString() + ", " + hand.getTile(handIdToNum.get(view.id)!! - 1).toString())
            }
            else if(handIdToNum.get(view.id)!! == 14) {

                hand.setDraw(tile)
                Log.d("debug", "change at draw, " + hand.getDraw().toString())
            }
            operationText.text = operationTextMap.get(currentPlayer)
            changeClaimButtonVisivility(View.GONE)
            //yakuText.text = Yaku.getYakuList(hand).toString()
        }
        updateView()
    }


    fun discardTile(id: Int){
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
            (tileStringToLayout.get(Tile.getTileIdTextByText(tileText))!!.getChildAt(0) as TextView).text = "あと" + wall.count(tile) + "枚"
            if(wall.count(tile) == 0)
                disableTile(tileStringToLayout.get(Tile.getTileIdTextByText(tileText))!!)
            discards.get(currentPlayer).pushHand(tile)
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(0) as ImageView).setImageResource(R.drawable.null_tile)
            (handNumToLayout.get(14 + currentPlayer)!!.getChildAt(1) as TextView).text = ""
        }

        updateView()
    }

    fun canClaim(): String{
        var claimType = ""
        if(discards.get((currentPlayer + 3) % 4).getHand().isNotEmpty()) {
            val previousDiscard = discards.get((currentPlayer + 3) % 4).getLast()
            val claimHand = hand.getHand().plusElement(previousDiscard).sortedWith(compareBy({ it.getType() }, { it.getNumber() })).toMutableList()
            if(hand.getDraw().getType() == Tile.Type.UNDEFINED){
                if(TileGroup.PUNG.getGroupListNum(claimHand) >= 1 && TileGroup.PUNG.getGroupList(claimHand).flatten().find { it.equals(previousDiscard) } != null){
                    pungButton.visibility = View.VISIBLE
                    claimType = claimType.plus("pung")
                }
                if(currentPlayer == 0) {
                    if (TileGroup.CHOW.getGroupListNum(claimHand) >= 1 && TileGroup.CHOW.getGroupList(claimHand).flatten().find { it.equals(previousDiscard) } != null) {
                        chowButton.visibility = View.VISIBLE
                        claimType = claimType.plus("chow")
                    }
                }
                if(TileGroup.KONG.getGroupListNum(claimHand) - TileGroup.KONG.getGroupListNum(hand.getHandWithDraw())  >= 1){
                    kongButton.visibility = View.VISIBLE
                    claimType = claimType.plus("kong")
                }
            }else{
                pungButton.visibility = View.GONE
                chowButton.visibility = View.GONE
                if (TileGroup.KONG.getGroupListNum(hand.getHandWithDraw()) >= 1) {
                    kongButton.visibility = View.VISIBLE
                    claimType = claimType.plus("kong")
                } else {
                    kongButton.visibility = View.GONE
                }
            }
        }
        return claimType
    }

    fun changePlayer(num: Int = -1){
        (handNumToLayout.get(14 + currentPlayer)!!.parent as LinearLayout).background = null
        if(num == -1){
            operationText.text = operationOrClaimTextMap.get(currentPlayer)
            currentPlayer = (currentPlayer + 1) % 4
            changeClaimButtonVisivility(View.VISIBLE, (currentPlayer + 3) % 4)
        }
        else {
            currentPlayer = num
            operationText.text = operationTextMap.get(currentPlayer)
            changeClaimButtonVisivility(View.GONE)
        }
        (handNumToLayout.get(14 + currentPlayer)!!.parent as LinearLayout).setBackgroundResource(R.drawable.border)

        canClaim()
    }

    fun changeSelectedHand(view: View, dp:Int){
        val layout = view as LinearLayout
        val layoutMarginParams = (layout.layoutParams as ViewGroup.MarginLayoutParams)
        layoutMarginParams.setMargins(layoutMarginParams.leftMargin, layoutMarginParams.topMargin, layoutMarginParams.rightMargin, layoutMarginParams.bottomMargin + dpToPx(dp))
        layout.layoutParams = layoutMarginParams
    }

    fun forceDraw(){
        if(!isKong && discards.get((currentPlayer + 3) % 4).getHand().size != 0 && discards.get((currentPlayer + 3) % 4).getLast().getType() != Tile.Type.UNDEFINED) {
            operationText.text = "あなたのツモ牌を選択 or 鳴きを選択してください"
            confirmButton.visibility = View.GONE
        }
        else {
            operationText.text = "あなたのツモ牌を選択"
            confirmButton.visibility = View.INVISIBLE
        }
        enableHandListener(false)
        enableTileListener(true)
    }

    fun execClaim(type: String, claimPlayer: Int){
        lateinit var claimTileList: MutableList<Tile>
        val preDiscardTile = discards.get((currentPlayer + 3) % 4).getLast()
        var concealedKong = false

        if(claimPlayer == 0){
            if(type.equals("pung")){
                val claimHand = hand.getHand().plusElement(preDiscardTile).sortedWith(compareBy({ it.getType() }, { it.getNumber() })).toMutableList()
                claimTileList = TileGroup.PUNG.getGroupList(claimHand).get(0)
            } else if(type.equals("chow")){
                //ここにチーの牌を選択する処理を書く
                val claimHand = hand.getHand().plusElement(preDiscardTile).sortedWith(compareBy({ it.getType() }, { it.getNumber() })).toMutableList()
                val chowNum = TileGroup.CHOW.getGroupListNum(claimHand)
                if(chowNum <= 1)
                    claimTileList = TileGroup.CHOW.getGroupList(claimHand).get(0)
                else{
                    val dialog = ClaimDialogFragment()
                    dialog.setFragment(this)
                    dialog.setClaimPlayer(claimPlayer)

                    val allChowList = TileGroup.CHOW.getGroupList(claimHand).distinctBy { it.get(0).toString() }
                    for(claimTiles in allChowList)
                        dialog.addChow(createClaimView("chow", claimPlayer, claimTiles, false, false), claimTiles)

                    dialog.show(fragmentManager!!, "ClaimDialogFragment")
                    return
                }
            } else if(type.equals("kong")){
                val claimHand = hand.getHandWithDraw().plusElement(preDiscardTile).sortedWith(compareBy({ it.getType() }, { it.getNumber() })).toMutableList()
                claimTileList = TileGroup.KONG.getGroupList(claimHand).get(0)
                if(TileGroup.KONG.getGroupListNum(hand.getHandWithDraw()) != 0)
                    concealedKong = true
            }
        } else{
            if(type.equals("pung")){
                claimTileList = mutableListOf(preDiscardTile, preDiscardTile, preDiscardTile)
            } else if(type.equals("chow")){

            } else if(type.equals("kong")){
                claimTileList = mutableListOf(preDiscardTile, preDiscardTile, preDiscardTile, preDiscardTile)
            }
        }
        createClaimView(type, claimPlayer, claimTileList, concealedKong, true)
    }

    fun createClaimView(type: String, claimPlayer: Int, claimTileList: MutableList<Tile>, concealedKong: Boolean, doAdd: Boolean): LinearLayout{
        if(doAdd)
            claimArea.visibility = View.VISIBLE

        val preDiscardTile = discards.get((currentPlayer + 3) % 4).getLast()
        var layoutContainer = LinearLayout(ContextGetter.applicationContext())
        layoutContainer.orientation = LinearLayout.HORIZONTAL

        var discardUseTile = Tile(Tile.Type.UNDEFINED, 0)
        for (index in (0..(claimTileList.size - 1))) {
            val layout = LinearLayout(ContextGetter.applicationContext())
            val imageView = ImageView(ContextGetter.applicationContext())
            val textView = TextView(ContextGetter.applicationContext())
            lateinit var handView: LinearLayout
            var imageId: Int

            if(claimPlayer == 0){
                if(discardUseTile.getType() == Tile.Type.UNDEFINED && claimTileList.get(index).equals(preDiscardTile))
                    handView = nullTile
                else
                    handView = handNumToLayout.get(hand.getHand().indexOf(claimTileList.get(index)) + 1)!!
                if((handView.getChildAt(1) as TextView).text.toString().equals("")){
                    imageId = resources.getIdentifier(Tile.getTileIdTextByText(preDiscardTile.toString()), "drawable", "com.example.nanshanten")
                } else{
                    imageId = resources.getIdentifier(Tile.getTileIdTextByText((handView!!.getChildAt(1) as TextView).text.toString()), "drawable", "com.example.nanshanten")
                }
            } else{
                handView = tileStringToLayout.get(Tile.getTileIdTextByText(claimTileList.get(index).toString()))!!
                if((handView.getChildAt(2) as TextView).text.toString().equals("")){
                    imageId = resources.getIdentifier(Tile.getTileIdTextByText(preDiscardTile.toString()), "drawable", "com.example.nanshanten")
                } else{
                    imageId = resources.getIdentifier(Tile.getTileIdTextByText((handView!!.getChildAt(2) as TextView).text.toString()), "drawable", "com.example.nanshanten")
                }
            }

            if(claimTileList.get(index).equals(preDiscardTile))
                discardUseTile = preDiscardTile
            layout.orientation = LinearLayout.VERTICAL
            imageView.setImageResource(imageId)
            imageView.layoutParams = if(doAdd) LinearLayout.LayoutParams(dpToPx(22), dpToPx(29)) else LinearLayout.LayoutParams(dpToPx(28), dpToPx(36))
            if(claimPlayer == 0){
                if((handView.getChildAt(1) as TextView).text.toString().equals(""))
                    textView.text = preDiscardTile.toString()
                else
                    textView.text = (handView.getChildAt(1) as TextView).text
            } else{
                if((handView.getChildAt(2) as TextView).text.toString().equals(""))
                    textView.text =  preDiscardTile.toString()
                else
                    textView.text = (handView.getChildAt(2) as TextView).text
            }

            if(doAdd)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8.0f)
            else
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
            textView.gravity = Gravity.CENTER

            if(doAdd){
                if(!concealedKong){
                    if((currentPlayer + 3) % 4 == (3 - index + claimPlayer) % 4) {
                        imageView.rotation = -90.0f
                        imageView.layoutParams = LinearLayout.LayoutParams(dpToPx(29), dpToPx(29))
                    }
                } else{
                    if(index == 0 || index == claimTileList.size - 1)
                        imageView.setImageResource(resources.getIdentifier("tile_back", "drawable", "com.example.nanshanten"))
                }
            }

            layout.addView(imageView)
            layout.addView(textView)

            val layoutMarginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) as ViewGroup.MarginLayoutParams
            layoutMarginParams.setMargins(dpToPx(1), dpToPx(5), dpToPx(1), dpToPx(0))
            layout.layoutParams = layoutMarginParams
            layoutContainer.addView(layout)
            val containerMarginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) as ViewGroup.MarginLayoutParams
            if(doAdd)
                containerMarginParams.setMargins(dpToPx(5), dpToPx(0), dpToPx(10), dpToPx(0))
            else
                containerMarginParams.setMargins(dpToPx(10), dpToPx(0), dpToPx(15), dpToPx(0))
            layoutContainer.layoutParams = containerMarginParams
        }
        if(doAdd){
            if(type.equals("kong") && !concealedKong){
                if(claimPlayer == (currentPlayer + 2) % 4){
                    val thirdTileLayout = layoutContainer.getChildAt(2)
                    val forthTileLayout = layoutContainer.getChildAt(3)
                    layoutContainer.removeViewAt(2)
                    layoutContainer.removeViewAt(2)
                    layoutContainer.addView(forthTileLayout)
                    layoutContainer.addView(thirdTileLayout)
                }
            }
            claimLayout.get(claimPlayer)!!.addView(layoutContainer)
            claimTileList.remove(discardUseTile)
            discards.get((currentPlayer + 3) % 4).popHand()
            var player = (currentPlayer + 3) % 4
            while(hand.getDraw().getType() == Tile.Type.UNDEFINED && player != claimPlayer){
                discards.get(player).pushHand(Tile(Tile.Type.UNDEFINED, 0))
                player = (player + 1) % 4
            }
            if(claimPlayer == 0){
                if (type.equals("pung")) {
                    hand.pung(claimTileList, discardUseTile)
                    changePlayer(claimPlayer)
                    forceDiscard()
                }
                if (type.equals("chow")) {
                    hand.chow(claimTileList, discardUseTile)
                    changePlayer(claimPlayer)
                    forceDiscard()
                }
                if (type.equals("kong")) {
                    isKong = true
                    hand.kong(claimTileList, discardUseTile)
                    changePlayer(claimPlayer)
                    forceDraw()
                }
            } else {
                if (type.equals("pung") || type.equals("chow")) {
                    changePlayer(player)
                }
                if (type.equals("kong")) {
                    changePlayer((player + 1) % 4)
                }
                wall.removeAll(claimTileList)
                claimTileList.forEach {
                    (tileStringToLayout.get(Tile.getTileIdTextByText(it.toString()))!!.getChildAt(0) as TextView).text = "あと" + wall.count(it) + "枚"
                    if(wall.count(it) == 0)
                        disableTile(tileStringToLayout.get(Tile.getTileIdTextByText(it.toString()))!!)
                }
            }
            if(currentPlayer != 0)
                changeClaimButtonVisivility(View.GONE)
            updateView()
        }
        return layoutContainer
    }

    fun forceDiscard(){
        if(Regex("kong").containsMatchIn(canClaim())){
            operationText.text = "手牌から1つ捨てる or カンしてください"
            confirmButton.visibility = View.GONE
        } else{
            operationText.text = "手牌から1つ捨ててください"
            confirmButton.visibility = View.INVISIBLE
        }
        enableHandListener(true)
        enableTileListener(false)
        drawArea.background = null
    }

    fun changeClaimButtonVisivility(param: Int, player: Int = -1){
        pungButton.visibility = View.GONE
        chowButton.visibility = View.GONE
        kongButton.visibility = View.GONE

        rightPungButton.visibility = param
        rightChowButton.visibility = param
        rightKongButton.visibility = param
        oppositePungButton.visibility = param
        oppositeChowButton.visibility = param
        oppositeKongButton.visibility = param
        leftPungButton.visibility = param
        leftChowButton.visibility = param
        leftKongButton.visibility = param

        if(param == View.VISIBLE)
            confirmButton.visibility = View.GONE
        else
            confirmButton.visibility = View.VISIBLE

        when(player){
            0 -> {
                oppositeChowButton.visibility = View.GONE
                leftChowButton.visibility = View.GONE
            }
            1 ->{
                rightPungButton.visibility = View.GONE
                rightChowButton.visibility = View.GONE
                rightKongButton.visibility = View.GONE
                leftChowButton.visibility = View.GONE
            }
            2 ->{
                oppositePungButton.visibility = View.GONE
                oppositeChowButton.visibility = View.GONE
                oppositeKongButton.visibility = View.GONE
                rightChowButton.visibility = View.GONE
            }
            3 ->{
                leftPungButton.visibility = View.GONE
                leftChowButton.visibility = View.GONE
                leftKongButton.visibility = View.GONE
                oppositeChowButton.visibility = View.GONE
                rightChowButton.visibility = View.GONE
            }
            else -> { }
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

        for(player in (0..3)){
            for(index in (0..discardView.get(player)!!.childCount - 1)){
                val discardLayout = (discardView.get(player)!!.getChildAt(index) as LinearLayout)
                if(index < discards.get(player).getHandWithoutNull().size){
                    val imageId = resources.getIdentifier(Tile.getTileIdTextByText(discards.get(player).getHandWithoutNull().get(index).toString()), "drawable", "com.example.nanshanten")
                    (discardLayout.getChildAt(0) as ImageView).setImageResource(imageId)
                    (discardLayout.getChildAt(1) as TextView).text = discards.get(player).getHandWithoutNull().get(index).toString()
                }
                else{
                    val imageId = resources.getIdentifier("null_tile", "drawable", "com.example.nanshanten")
                    (discardLayout.getChildAt(0) as ImageView).setImageResource(imageId)
                    (discardLayout.getChildAt(1) as TextView).text = ""
                }
            }
        }
    }

    fun getDragImageId(dragId: Int): Int{
        return resources.getIdentifier(Tile.getTileIdText(dragId), "drawable", "com.example.nanshanten")
    }

    fun dpToPx(dp: Int): Int{
        return Math.round(resources.displayMetrics.density * dp)
    }

    fun disableTile(layout: LinearLayout){
        (layout.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(ContextGetter.applicationContext(), R.color.colorAccent))
        (layout.getChildAt(1) as ImageView).alpha = 0.1f
    }

    fun enableHandListener(flag: Boolean){
        if(flag){
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
            handDraw.setOnClickListener(handClickListener)
        }
        else{
            hand1.setOnClickListener(null)
            hand2.setOnClickListener(null)
            hand3.setOnClickListener(null)
            hand4.setOnClickListener(null)
            hand5.setOnClickListener(null)
            hand6.setOnClickListener(null)
            hand7.setOnClickListener(null)
            hand8.setOnClickListener(null)
            hand9.setOnClickListener(null)
            hand10.setOnClickListener(null)
            hand11.setOnClickListener(null)
            hand12.setOnClickListener(null)
            hand13.setOnClickListener(null)
            handDraw.setOnClickListener(null)
        }
    }

    fun enableTileListener(flag: Boolean){
        if(flag){
            tile_character1.setOnClickListener(tileClickListener)
            tile_character2.setOnClickListener(tileClickListener)
            tile_character3.setOnClickListener(tileClickListener)
            tile_character4.setOnClickListener(tileClickListener)
            tile_character5.setOnClickListener(tileClickListener)
            tile_character6.setOnClickListener(tileClickListener)
            tile_character7.setOnClickListener(tileClickListener)
            tile_character8.setOnClickListener(tileClickListener)
            tile_character9.setOnClickListener(tileClickListener)
            tile_circle1.setOnClickListener(tileClickListener)
            tile_circle2.setOnClickListener(tileClickListener)
            tile_circle3.setOnClickListener(tileClickListener)
            tile_circle4.setOnClickListener(tileClickListener)
            tile_circle5.setOnClickListener(tileClickListener)
            tile_circle6.setOnClickListener(tileClickListener)
            tile_circle7.setOnClickListener(tileClickListener)
            tile_circle8.setOnClickListener(tileClickListener)
            tile_circle9.setOnClickListener(tileClickListener)
            tile_bamboo1.setOnClickListener(tileClickListener)
            tile_bamboo2.setOnClickListener(tileClickListener)
            tile_bamboo3.setOnClickListener(tileClickListener)
            tile_bamboo4.setOnClickListener(tileClickListener)
            tile_bamboo5.setOnClickListener(tileClickListener)
            tile_bamboo6.setOnClickListener(tileClickListener)
            tile_bamboo7.setOnClickListener(tileClickListener)
            tile_bamboo8.setOnClickListener(tileClickListener)
            tile_bamboo9.setOnClickListener(tileClickListener)
            tile_east.setOnClickListener(tileClickListener)
            tile_south.setOnClickListener(tileClickListener)
            tile_west.setOnClickListener(tileClickListener)
            tile_north.setOnClickListener(tileClickListener)
            tile_whiteDragon.setOnClickListener(tileClickListener)
            tile_greenDragon.setOnClickListener(tileClickListener)
            tile_redDragon.setOnClickListener(tileClickListener)
        }else{
            tile_character1.setOnClickListener(null)
            tile_character2.setOnClickListener(null)
            tile_character3.setOnClickListener(null)
            tile_character4.setOnClickListener(null)
            tile_character5.setOnClickListener(null)
            tile_character6.setOnClickListener(null)
            tile_character7.setOnClickListener(null)
            tile_character8.setOnClickListener(null)
            tile_character9.setOnClickListener(null)
            tile_circle1.setOnClickListener(null)
            tile_circle2.setOnClickListener(null)
            tile_circle3.setOnClickListener(null)
            tile_circle4.setOnClickListener(null)
            tile_circle5.setOnClickListener(null)
            tile_circle6.setOnClickListener(null)
            tile_circle7.setOnClickListener(null)
            tile_circle8.setOnClickListener(null)
            tile_circle9.setOnClickListener(null)
            tile_bamboo1.setOnClickListener(null)
            tile_bamboo2.setOnClickListener(null)
            tile_bamboo3.setOnClickListener(null)
            tile_bamboo4.setOnClickListener(null)
            tile_bamboo5.setOnClickListener(null)
            tile_bamboo6.setOnClickListener(null)
            tile_bamboo7.setOnClickListener(null)
            tile_bamboo8.setOnClickListener(null)
            tile_bamboo9.setOnClickListener(null)
            tile_east.setOnClickListener(null)
            tile_south.setOnClickListener(null)
            tile_west.setOnClickListener(null)
            tile_north.setOnClickListener(null)
            tile_whiteDragon.setOnClickListener(null)
            tile_greenDragon.setOnClickListener(null)
            tile_redDragon.setOnClickListener(null)
        }
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

        enableTileListener(true)
        forceDraw()

        pungButton.setOnClickListener(claimButtonListener("pung"))
        chowButton.setOnClickListener(claimButtonListener("chow"))
        kongButton.setOnClickListener(claimButtonListener("kong"))
        confirmButton.setOnClickListener(confirmButtonListener)
        rightPungButton.setOnClickListener(opponentClaimButtonListener("pung", 1))
        rightChowButton.setOnClickListener(opponentClaimButtonListener("chow", 1))
        rightKongButton.setOnClickListener(opponentClaimButtonListener("kong", 1))
        oppositePungButton.setOnClickListener(opponentClaimButtonListener("pung", 2))
        oppositeChowButton.setOnClickListener(opponentClaimButtonListener("chow", 2))
        oppositeKongButton.setOnClickListener(opponentClaimButtonListener("kong", 2))
        leftPungButton.setOnClickListener(opponentClaimButtonListener("pung", 3))
        leftChowButton.setOnClickListener(opponentClaimButtonListener("chow", 3))
        leftKongButton.setOnClickListener(opponentClaimButtonListener("kong", 3))

        handIdToNum = mapOf(hand1.id to 1, hand2.id to 2, hand3.id to 3, hand4.id to 4, hand5.id to 5, hand6.id to 6, hand7.id to 7,
            hand8.id to 8, hand9.id to 9, hand10.id to 10, hand11.id to 11, hand12.id to 12, hand13.id to 13, handDraw.id to 14,
        discardRightPlayer.id to 15, discardOppositePlayer.id to 16, discardLeftPlayer.id to 17)

        handNumToLayout = mutableMapOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handIdToNum.forEach { id, num -> handNumToLayout.set(num, activity!!.findViewById(id)) }
        }

        claimLayout = mapOf(0 to playerClaim, 1 to rightClaim, 2 to oppositeClaim, 3 to leftClaim)

        tileStringToLayout = mapOf("character1" to tile_character1, "character2" to tile_character2, "character3" to tile_character3,
            "character4" to tile_character4, "character5" to tile_character5, "character6" to tile_character6, "character7" to tile_character7,
            "character8" to tile_character8, "character9" to tile_character9, "circle1" to tile_circle1, "circle2" to tile_circle2, "circle3" to tile_circle3,
            "circle4" to tile_circle4, "circle5" to tile_circle5, "circle6" to tile_circle6, "circle7" to tile_circle7,
            "circle8" to tile_circle8, "circle9" to tile_circle9, "bamboo1" to tile_bamboo1, "bamboo2" to tile_bamboo2, "bamboo3" to tile_bamboo3,
            "bamboo4" to tile_bamboo4, "bamboo5" to tile_bamboo5, "bamboo6" to tile_bamboo6, "bamboo7" to tile_bamboo7,
            "bamboo8" to tile_bamboo8, "bamboo9" to tile_bamboo9, "east" to tile_east, "south" to tile_south, "west" to tile_west, "north" to tile_north,
            "whitedragon" to tile_whiteDragon, "greendragon" to tile_greenDragon, "reddragon" to tile_redDragon)

        discardView = mapOf(0 to discardPlayerTiles, 1 to discardRightTiles, 2 to discardOppositeTiles, 3 to discardLeftTiles)
        touchHand = nullTile

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

        changeClaimButtonVisivility(View.GONE)
        forceDraw()
        confirmButton.visibility = View.INVISIBLE

        wall.removeAll(hand.getHand())
        hand.getHand().forEach {
            (tileStringToLayout.get(Tile.getTileIdTextByText(it.toString()))!!.getChildAt(0) as TextView).text = "あと" + wall.count(it) + "枚"
        }

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
