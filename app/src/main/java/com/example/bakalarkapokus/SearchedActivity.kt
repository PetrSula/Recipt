package com.example.bakalarkapokus

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Recept.CategoryAdapter
import com.example.bakalarkapokus.Recept.SearchAdapter
import com.example.bakalarkapokus.Recept.surovinyAdapter
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.ItemAdapter
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_vyhledavani.*
import kotlinx.android.synthetic.main.ingredience_main.*
import kotlinx.android.synthetic.main.recept_postup.*
import kotlinx.android.synthetic.main.selected_rv.*


class SearchedActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gv_title = intent.getStringExtra("EXTRA_TITLE")
        val arraySearched = intent.getSerializableExtra("EXTRA_SEARCHED") as ArrayList<SQLdata.AraySearched>


        setContentView(R.layout.selected_rv)
        //        SET TITLE
        val title = findViewById<TextView>(R.id.tvtitleSelected)
        if (gv_title.isNullOrBlank()) {
            title.text= ""
        }else{
            title.text = gv_title
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.Dlsearch)
        val navView = findViewById<NavigationView>(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> {
                    val intent = Intent(this, DruhaAktivita::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
//                R.id.miItem2 -> {
//                    val intent = Intent(this, ReceptActivita::class.java)
//                    startActivity(intent)
//                    true
//                }
                R.id.miItem3 ->{
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.miItem0 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("EXIT",true)
                true
            }
                else -> false
            }
        }

        setupListofDataIntoRecyclerView(arraySearched)

    }

    private fun setupListofDataIntoRecyclerView(array : ArrayList<SQLdata.AraySearched>){
        rv_Searched.layoutManager = LinearLayoutManager(this)
        val itemAdapter = SearchAdapter(this,array)
        rv_Searched.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object : SearchAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                Intent(this@SearchedActivity,ReceptActivita::class.java).also {
                    it.putExtra("EXTRA_ID", array[position].id)
                    startActivity(it)
                }
            }

        })
    }



}