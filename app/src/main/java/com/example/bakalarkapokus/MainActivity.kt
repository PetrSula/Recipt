package com.example.bakalarkapokus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Recept.CategoryAdapter
import com.example.bakalarkapokus.Recept.SearchAdapter
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.acSearch
import kotlinx.android.synthetic.main.activity_main.ivSearch
import kotlinx.android.synthetic.main.recept_postup.*
import kotlinx.android.synthetic.main.selected_rv.*

/* TODO - Přidat možnost vyhledávání receptu, jejich obrázek
        - ošetřt přidávání jakéhokoliv textu s mezerami.
        - live of activity
        - přenesení SQL lite a její rozšíření případné

 */
fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var imageId: Array<Int>
    lateinit var name: Array<String>
    lateinit var titlesLV : ListView
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var titlesList : ArrayList<String>
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_Cathegory()
        rv_type()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
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
// SearchView
//        titlesLV = findViewById(R.id.listsearch)
//        searchView = findViewById(R.id.simpleSearchView)
//        titlesList = DBHelper(this).selectallTitles()
//        listAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,titlesList)
//        titlesLV.adapter = listAdapter
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (titlesList.contains(query)){
//                    listAdapter.filter.filter(query)
//                }
//                else{
//                    Toast.makeText(this@MainActivity, "Nic nenalezeno", Toast.LENGTH_LONG).show()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                listAdapter.filter.filter(newText)
//                return false
//            }
//        })m
//         AutoCoplitetextView
        val autoTextView : AutoCompleteTextView = findViewById(R.id.acSearch)
        var listOfRecepts= DBHelper(this).selectAllTitles()
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfRecepts)
        autoTextView.threshold=1
        autoTextView.setAdapter(Autoadapter)

//      Search on clicl listener
        ivSearch.setOnClickListener{
            search_handeler()
        }
//        add Recept
        fbAdd.setOnClickListener {
            val intent = Intent(this, AddRecept::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun search_handeler(){
        val input = acSearch.text.toString().trim()
//        if (input.isEmpty()){
//            return
//        }
        val id = DBHelper(this).selectByTitle(input)
        if (id != 0){
            showRecept(id)
        }
        else{
            return
        }
    }
    fun showRecept(id:Int){
        Intent(this,ReceptActivita::class.java).also {
            it.putExtra("EXTRA_ID", id)
            startActivity(it)
        }
    }
    fun showSearched(){
        val where = " "
        var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
        arraySearched = DBHelper(this).selectTitleIMG(where)
        Intent(this,SearchedActivity::class.java).also {
            it.putExtra("EXTRA_SEARCHED",arraySearched)
            startActivity(it)
        }
    }
    fun rv_Cathegory(){
        val category = arrayListOf<SQLdata.ArrayCategory>()
        imageId = arrayOf(
            R.drawable.steak,
            R.drawable.vegetarian,
            R.drawable.vegan,
            R.drawable.low_carb
        )
        val stringCategory = resources.getStringArray(R.array.categoryRecept)
        for (i in imageId.indices){
            val item = SQLdata.ArrayCategory(i,stringCategory[i],imageId[i])
            category.add(item)
        }

        rv_Category.layoutManager = LinearLayoutManager(this)
        var categoryAdapter = CategoryAdapter(category)
        rv_Category.adapter = categoryAdapter
        categoryAdapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
//                super.onItemClick(position)
//                val intent = Intent(this@MainActivity,SearchedActivity::class.java).also {
//                    it.putExtra("EXTRA_SEARCHED",arraySearched)
//                    startActivity(it)
                val where = "AND (RECEPT.CATEGORY = " + "'"+category[position].string+"')"
                var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                arraySearched = DBHelper(this@MainActivity).selectTitleIMG(where)
                Intent(this@MainActivity,SearchedActivity::class.java).also {
                    it.putExtra("EXTRA_SEARCHED",arraySearched)
                    it.putExtra("EXTRA_TITLE",category[position].string)
                    startActivity(it)
                }
            }
        })
    }

    fun rv_type(){
        var type = arrayListOf<SQLdata.ArrayCategory>()
        imageId = arrayOf(
            R.drawable.hlavni_chod,
            R.drawable.moucnik,
            R.drawable.polevka,
            R.drawable.svacina,
            R.drawable.snidane,
            R.drawable.priloha
        )
        val stringType = resources.getStringArray(R.array.typeOfRecept)
        for (i in imageId.indices){
            val item = SQLdata.ArrayCategory(i,stringType[i],imageId[i])
            type.add(item)
        }

        rv_Type.layoutManager = LinearLayoutManager(this)
        var categoryAdapter = CategoryAdapter(type)
        rv_Type.adapter = categoryAdapter

        categoryAdapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
//                super.onItemClick(position)
//                val intent = Intent(this@MainActivity,SearchedActivity::class.java).also {
//                    it.putExtra("EXTRA_SEARCHED",arraySearched)
//                    startActivity(it)
                val where = " AND RECEPT.TYPE = " + "'"+type[position].string+"'"
                var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                arraySearched = DBHelper(this@MainActivity).selectTitleIMG(where)
                Intent(this@MainActivity,SearchedActivity::class.java).also {
                    it.putExtra("EXTRA_SEARCHED",arraySearched)
                    it.putExtra("EXTRA_TITLE",type[position].string)
                    startActivity(it)
                }
            }
        })
    }
}