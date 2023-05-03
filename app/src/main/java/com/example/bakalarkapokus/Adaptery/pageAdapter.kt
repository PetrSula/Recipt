package com.example.bakalarkapokus.Adaptery

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bakalarkapokus.fragments.DayFragment
import com.example.bakalarkapokus.fragments.WeekFragment
import com.google.android.material.tabs.TabLayout

internal class pageAdapter (var context: Context, fm: FragmentManager, var totalTabs: Int , date : String): FragmentPagerAdapter(fm) {
    val gv_date = date
    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> {
                DayFragment(gv_date)
            }
            1->{
                WeekFragment(gv_date)
            }
            else -> getItem(position)
        }
    }
}