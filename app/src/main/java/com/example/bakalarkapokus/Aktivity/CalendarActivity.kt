package com.example.bakalarkapokus.Aktivity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.bakalarkapokus.Adaptery.pageAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.example.bakalarkapokus.fragments.DayFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_jidenicek.*
import kotlinx.android.synthetic.main.fragment_day.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class CalendarActivity: AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    lateinit var toggle: ActionBarDrawerToggle


    class Myclass{
        companion object{
            var activity: Activity? = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jidenicek)
        var gv_datum    = intent.getStringExtra("EXTRA_DATE")

        Myclass.activity = this@CalendarActivity


        val drawerlayout = findViewById<DrawerLayout>(R.id.drawerLayout_meat)
        val navView = findViewById<NavigationView>(R.id.navView)

        val toggle = ActionBarDrawerToggle(this, drawerlayout, R.string.open,R.string.close )
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerlayout.setDrawerListener(toggle)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> {
                    val intent = Intent(this, SpizActivity::class.java)
                    startActivity(intent)
                    finish()
                    drawerlayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem2 -> {
                    val where = "  RECEPT.ID <> 0 "
                    var arraySearched: ArrayList<SQLdata.AraySearched> =
                        ArrayList<SQLdata.AraySearched>()
                    arraySearched = DBHelper(this).selectTitleIMG(where)
                    Intent(this, SearchedActivity::class.java).also {
                        it.putExtra("EXTRA_SEARCHED", arraySearched)
                        finish()
                        startActivity(it)
                    }
                    drawerlayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem3 -> {
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
                    drawerlayout.closeDrawer(GravityCompat.START)
                    finish()
                    true
                }R.id.miItem0 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("EXIT",true)
                startActivity(intent)
                true
                }
                else -> false
            }
        }
        if (gv_datum == null){
            gv_datum = "0000-00-00"
        }


        tabLayout = findViewById(R.id.Tl_cal_period)
        viewPager = findViewById(R.id.vp_cal_pager)

        tabLayout.addTab(tabLayout.newTab().setText("Den"))
        tabLayout.addTab(tabLayout.newTab().setText("Týden"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = pageAdapter(this, supportFragmentManager, 2,gv_datum )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                val position = tab?.position
                if (position != null) {
                    when (position) {
                        0 -> {
                            // Update data in first fragment
                        }
                        1 -> {
                            // Update data in second fragment
                        }
                        // Add more cases for additional tabs
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    if (id == android.R.id.home) {
        if (drawerLayout_meat.isDrawerOpen(GravityCompat.START)) {
            drawerLayout_meat.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout_meat.openDrawer(GravityCompat.START)
        }
        return true
    }

    return super.onOptionsItemSelected(item)
}
}