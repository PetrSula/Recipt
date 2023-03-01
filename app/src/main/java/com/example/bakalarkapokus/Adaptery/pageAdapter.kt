package com.example.bakalarkapokus.Adaptery

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bakalarkapokus.fragments.DayFragment
import com.example.bakalarkapokus.fragments.WeekFragment
import com.google.android.material.tabs.TabLayout

internal class pageAdapter (var context: Context, fm: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> {
                DayFragment()
            }
            1->{
                WeekFragment()
            }
            else -> getItem(position)
        }
    }
}