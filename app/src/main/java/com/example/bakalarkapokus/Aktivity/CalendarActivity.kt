package com.example.bakalarkapokus.Aktivity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.bakalarkapokus.Adaptery.pageAdapter
import com.example.bakalarkapokus.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_day.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class CalendarActivity: AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jidenicek)

        tabLayout  = findViewById(R.id.Tl_cal_period)
        viewPager = findViewById(R.id.vp_cal_pager)

        tabLayout.addTab(tabLayout.newTab().setText("Den"))
        tabLayout.addTab(tabLayout.newTab().setText("Týden"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = pageAdapter(this, supportFragmentManager, 2)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

//        val Monday = DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.getDefault())
//        date.text = Monday

    }
}