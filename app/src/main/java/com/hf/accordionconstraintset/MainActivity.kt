package com.hf.accordionconstraintset

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.HORIZONTAL
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.R.layout
import android.graphics.Color
import android.os.Build
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.TextView
import java.util.*


class MainActivity : AppCompatActivity() {

    private val guidelineMap = hashMapOf<Int, Guideline>()
    private val tiles = mutableListOf<Tile>()
    private val tileMap = hashMapOf<Int, Tile>()
    private lateinit var topGuide : Guideline
    lateinit var bottomGuide : Guideline
    lateinit var onClickListener : View.OnClickListener
    private val tileCount = 5

    /**
     *      more than 10 payment types (???)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onClickListener = View.OnClickListener {
            handleClick(it)
        }

        topGuide = createGuideline(this, HORIZONTAL)
        root.addView(topGuide)
        topGuide.setGuidelinePercent(0.1f)

        bottomGuide = createGuideline(this, HORIZONTAL)
        root.addView(bottomGuide)
        bottomGuide.setGuidelinePercent(0.9f)

        generateGuides(tileCount)
        generateTiles(tileCount)
        render()


    }

    private fun handleClick(it: View?) {

        it?.id?.let { id ->
            tileMap[id]?.let { tile ->

                if (tile.isTop) {
                    render(true)
                    for (t in tiles) {
                        t.isTop = false
                        tileMap[t.view.id] = t
                    }
                }
                else {

                    for (t in tiles) {
                        t.isTop = t.view.id == id
                        tileMap[t.view.id] = t
                    }


                    var tileId :Int? = null
                    var foundTile : Tile? = null
                    var foundIndex : Int? = null

                    for (i in 0 until tiles.count()) {
                        val current = tiles[i]

                        if (current.view.id == id) {
                            tileId = current.view.id
                            foundTile = current
                            foundIndex = i
                            break
                        }
                    }

                    //view was found
                    if (tileId != null &&
                        foundTile != null &&
                        foundIndex != null &&
                        tiles.isNotEmpty()) {

                        //render selected To Top
                        renderToTop(foundTile.view)

                        //more than one view but selected is last
                        if (tileId == tiles.last().view.id) {

                            val bottomId = tiles[foundIndex - 1].view.id
                            //pick penultimate as bottom
                            renderToBottom(tiles[foundIndex - 1].view)
                            //hide other
                            for (t in tiles) {
                                if (!listOf(id, bottomId).contains(t.view.id)) {
                                    renderAsHidden(t.view)
                                }
                            }
                        }
                        else {

                            val bottomId = tiles[foundIndex + 1].view.id
                            //pick next as bottom
                            renderToBottom(tiles[foundIndex + 1].view)
                            //hide other
                            for (t in tiles) {
                                if (!listOf(id, bottomId).contains(t.view.id)) {
                                    renderAsHidden(t.view)
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun renderToTop(tv : TextView) {

        val set = ConstraintSet()
        set.clone(root)

        set.connect(tv.id, ConstraintSet.TOP, root.id, ConstraintSet.TOP)
        set.connect(tv.id, ConstraintSet.BOTTOM, topGuide.id, ConstraintSet.TOP)
        set.connect(tv.id, ConstraintSet.LEFT, root.id, ConstraintSet.LEFT)
        set.connect(tv.id, ConstraintSet.RIGHT, root.id, ConstraintSet.RIGHT)
        set.constrainWidth(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        set.constrainHeight(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val transition = AutoTransition();
            transition.duration = 350
            TransitionManager.beginDelayedTransition(root, transition)
            set.applyTo(root)
        }
    }

    private fun renderToBottom(tv : TextView) {

        val set = ConstraintSet()
        set.clone(root)

        set.connect(tv.id, ConstraintSet.TOP, bottomGuide.id, ConstraintSet.BOTTOM)
        set.connect(tv.id, ConstraintSet.BOTTOM, root.id, ConstraintSet.BOTTOM)
        set.connect(tv.id, ConstraintSet.LEFT, root.id, ConstraintSet.LEFT)
        set.connect(tv.id, ConstraintSet.RIGHT, root.id, ConstraintSet.RIGHT)
        set.constrainWidth(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        set.constrainHeight(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val transition = AutoTransition();
            transition.duration = 350
            TransitionManager.beginDelayedTransition(root, transition)
            set.applyTo(root)
        }
    }

    private fun renderAsHidden(tv : TextView) {

        val set = ConstraintSet()
        set.clone(root)

        set.connect(tv.id, ConstraintSet.TOP, root.id, ConstraintSet.BOTTOM)
        set.connect(tv.id, ConstraintSet.BOTTOM, root.id, ConstraintSet.BOTTOM)
        set.connect(tv.id, ConstraintSet.LEFT, root.id, ConstraintSet.LEFT)
        set.connect(tv.id, ConstraintSet.RIGHT, root.id, ConstraintSet.RIGHT)
        set.constrainWidth(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        set.constrainHeight(tv.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val transition = AutoTransition();
            transition.duration = 350
            TransitionManager.beginDelayedTransition(root, transition)
            set.applyTo(root)
        }
    }

    private fun generateTiles(count : Int) {

        for (t in 0 until count) {
            val tv = createTextView("tile$t")
            val tile = Tile(tv, false)
            tiles.add(tile)
            tv.setOnClickListener(onClickListener)
            tileMap[tv.id] = tile
        }
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    private fun render(animated : Boolean = false) {

        val set = ConstraintSet()
        set.clone(root)

        for (i in 0 until tiles.count()) {

            val tv2 = tiles[i].view

            val upIndex = 10 - (i + 1)
            val upGuide = guidelineMap[upIndex]
            val downGuide = guidelineMap[upIndex + 1]

            if (upGuide != null && downGuide != null) {
                set.connect(tv2.id, ConstraintSet.TOP, upGuide.id, ConstraintSet.BOTTOM)
                set.connect(tv2.id, ConstraintSet.BOTTOM, downGuide.id, ConstraintSet.TOP)
                set.connect(tv2.id, ConstraintSet.LEFT, root.id, ConstraintSet.LEFT)
                set.connect(tv2.id, ConstraintSet.RIGHT, root.id, ConstraintSet.RIGHT)
                set.constrainWidth(tv2.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
                set.constrainHeight(tv2.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
            }

        }

        if (animated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val transition = AutoTransition()
                transition.duration = 350
                TransitionManager.beginDelayedTransition(root, transition)
                set.applyTo(root)
            }
        }
        else {
            set.applyTo(root)
        }

    }

    private fun createTextView(title : String) : TextView {
        val tv2 = TextView(this)
        tv2.id = ViewCompat.generateViewId()
        tv2.setBackgroundColor(getRandomColor())
        tv2.text = title
        tv2.textSize = 29f
        root.addView(tv2)

        return tv2
    }

    private fun generateGuides(count : Int) {

        for (i in 0..count) {
            val guideline = createGuideline(this, HORIZONTAL)
            root.addView(guideline)
            val percent = 1 - (0.1 * i)
            guideline.setGuidelinePercent(percent.toFloat())
            guideline.tag = 10 - i
            guidelineMap[10 - i] = guideline
        }

    }

    private fun createGuideline(context: Context, orientation: Int): Guideline {
        val guideline = Guideline(context)
        guideline.id = ViewCompat.generateViewId()
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        lp.orientation = orientation
        guideline.layoutParams = lp

        return guideline
    }

    data class Tile(var view : TextView, var isTop : Boolean)
}
