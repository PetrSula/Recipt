package com.example.bakalarkapokus.Aktivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.SearchAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_searched.*


class SearchedActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    class SearchableMyclass{
        companion object{
            var activity: Activity? = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchableMyclass.activity = this@SearchedActivity
        val gv_title = intent.getStringExtra("EXTRA_TITLE")
        val arraySearched = intent.getSerializableExtra("EXTRA_SEARCHED") as ArrayList<SQLdata.AraySearched>


        setContentView(R.layout.activity_searched)
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
                    val intent = Intent(this, SpizActivity::class.java)
                    startActivity(intent)
                    finish()
                    drawerLayout.closeDrawer(GravityCompat.START)
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
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem0 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("EXIT",true)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }R.id.miItem4 ->{
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
                else -> false
            }
        }
        fb_search_Add.setOnClickListener {
            val intent = Intent(this, AddRecept::class.java)
            startActivity(intent)
        }
//                 AutoCoplitetextView
        val autoTextView : AutoCompleteTextView = findViewById(R.id.ac_Srch_Search)
        var listOfRecepts= DBHelper(this).selectAllTitles()
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfRecepts)
        autoTextView.threshold=1
        autoTextView.setAdapter(Autoadapter)
        iv_srch_Search.setOnClickListener{
            search_handeler()
        }
        setupListofDataIntoRecyclerView(arraySearched)

    }

    private fun search_handeler() {
        hideKeyboard(this)
        var arraySearched:ArrayList<SQLdata.AraySearched>
        val input = ac_Srch_Search.text.toString().trim()
        if (input.isEmpty()){
            Toast.makeText(applicationContext, "Nezadali jste žádné hodnoty", Toast.LENGTH_LONG).show()
            return
        }
        arraySearched = DBHelper(this).selectByTitle(input)
        if (arraySearched.size == 1){
            showRecept(arraySearched[0].id)
        }else if (arraySearched.size != 0){
            setupListofDataIntoRecyclerView(arraySearched)
        }else{
            Toast.makeText(applicationContext, "Zadané hodnotě neodpovídá žádný recept", Toast.LENGTH_LONG).show()
            return
        }
    }
    fun edit(){
        Toast.makeText(applicationContext, "Něco se děje", Toast.LENGTH_LONG).show()
        return
    }

    private fun showRecept(id: Int) {
        Intent(this,ReceptActivity::class.java).also {
            it.putExtra("EXTRA_ID", id)
            startActivity(it)
            finish()
        }
    }


    private fun setupListofDataIntoRecyclerView(array : ArrayList<SQLdata.AraySearched>){
        rv_Searched.layoutManager = LinearLayoutManager(this)
        val itemAdapter = SearchAdapter(this,array)
        rv_Searched.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object : SearchAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                Intent(this@SearchedActivity,ReceptActivity::class.java).also {
                    it.putExtra("EXTRA_ID", array[position].id)
                    startActivity(it)
                }
            }

        })
    }



}