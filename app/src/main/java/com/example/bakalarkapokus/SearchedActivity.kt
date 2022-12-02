package com.example.bakalarkapokus

import android.content.Intent
import android.os.Bundle
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

//        SET TITLE



        setContentView(R.layout.selected_rv)
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
                    true
                }
                R.id.miItem2 -> {
                    val intent = Intent(this, ReceptActivita::class.java)
                    startActivity(intent)
                    true
                }
                R.id.miItem3 ->{
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
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