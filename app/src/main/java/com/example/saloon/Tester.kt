package com.example.saloon

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tester : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)

        val llTester = findViewById<LinearLayout>(R.id.llTester)
        val dragView = View(this)
        dragView.background = AppCompatResources.getDrawable(this,R.drawable.rounded)
        dragView.setBackgroundColor((getColor(R.color.red)))
        val dimen = resources.getDimensionPixelSize(R.dimen.item_width)
        dragView.layoutParams = ViewGroup.LayoutParams(dimen,dimen)

        llTester.addView(dragView,1)

        val rvTest = findViewById<RecyclerView>(R.id.rvTest)
        rvTest.layoutManager = LinearLayoutManager(this)
        val r = mutableListOf<Int>()
        for (i in 0 until 5){r.add(i)}
        rvTest.adapter = TestAdapter(r)
        dragView.setOnLongClickListener {
            val text = "Sup Dawg!"
            val item = ClipData.Item(text)
            val mimeType = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(text,mimeType,item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)

//            it.visibility = View.INVISIBLE
            rvTest.adapter?.notifyItemRangeChanged(0,20)
            true
        }
        rvTest.adapter?.notifyItemRangeChanged(0,20)

        val dragListListener = View.OnDragListener { view, event ->
            when (event.action){
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
//                    val item = event.clipData.getItemAt(0)
//                    val dragData = item.text
//                    println("worked")
//
                    val v = event.localState as View
                    val owner = v.parent as ViewGroup
                    owner.removeView(v)
                    owner.addView(v)
//                    val destination = view as LinearLayout
//
//                    destination.addView(v)
                    v.visibility = View.VISIBLE
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    true
                }
                else -> false

            }
        }
        llTester.setOnDragListener(dragListListener)
    }
}