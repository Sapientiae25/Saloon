package com.example.saloon

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs


class Test : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)

        val sliderHandler = Handler(Looper.getMainLooper())
        val vpImage = findViewById<ViewPager2>(R.id.vpImage)
        val tab = findViewById<TabLayout>(R.id.tab)

        val images = arrayOf(1,2,3,4,5,6)
        val adapter = SliderAdapter(this,images)
        vpImage.adapter = adapter
        vpImage.clipChildren = false
        vpImage.clipToPadding = false
        vpImage.offscreenPageLimit = 3
        vpImage.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position -> val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f }

        vpImage.setPageTransformer(compositePageTransformer)
        val sliderRunnable = Runnable { vpImage.currentItem = if (vpImage.currentItem+1 == images.size) 0 else vpImage.currentItem+1}

        TabLayoutMediator(tab,vpImage) { _, _ -> }.attach()

        vpImage.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 2000) } })
    }
}