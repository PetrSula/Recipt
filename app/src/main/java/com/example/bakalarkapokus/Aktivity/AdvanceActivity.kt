package com.example.bakalarkapokus.Aktivity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.SurovinyAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_search_advance.*

val data_search = ArrayList<SQLdata.Suroviny>()
val data_id     = ArrayList<Int>()
var id_inger = 0

class AdvanceActivity : AppCompatActivity(){
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data_search.clear()
        data_id.clear()

        setContentView(R.layout.activity_search_advance)
//        val drawerLayout = findViewById<DrawerLayout>(R.id.Dlsearch)
//        val navView = findViewById<NavigationView>(R.id.navView)
        val drawerLayou = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navVi = findViewById<NavigationView>(R.id.navView)


        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close )
        drawerLayou.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> {
                    val intent = Intent(this, SpizActivity::class.java)
                    finish()
                    startActivity(intent)
                    true
                }
                R.id.miItem2 -> {
                    val where = " RECEPT.ID <> 0 "
                    var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                    arraySearched = DBHelper(this@AdvanceActivity).selectTitleIMG(where)
                    Intent(this@AdvanceActivity,SearchedActivity::class.java).also {
                        it.putExtra("EXTRA_SEARCHED", arraySearched)
                        startActivity(it)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem0 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("EXIT",true)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem3 ->{
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
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
        val autoTextView : AutoCompleteTextView = findViewById(R.id.at_AddSurovinaAV)
        var listOfIngredience = DBHelper(this).selectallIngredience()
//   REciclewiew
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfIngredience)
        autoTextView.threshold=1
        autoTextView.setAdapter(Autoadapter)
//                adapter pro dropdown Kategorie
        val stringCategory = resources.getStringArray(R.array.categoryRecept)
        val newStringCategory = stringCategory.copyOf(stringCategory.size+1)
        newStringCategory.set(stringCategory.size, "")
        val categoryAdapter = ArrayAdapter(this,R.layout.dropdown_item,newStringCategory)
        val categoryAC = findViewById<AutoCompleteTextView>(R.id.ac_categoryAV)
        categoryAC.setAdapter(categoryAdapter)
//        adapter pro dropdown Typ
        val stringType = resources.getStringArray(R.array.typeOfRecept)
        val newStringType = stringType.copyOf(stringType.size+1)
        newStringType.set(stringType.size, "")
        val typeAdapter = ArrayAdapter(this, R.layout.dropdown_item, newStringType)
        val typeAC = findViewById<AutoCompleteTextView>(R.id.ac_typeAV)
        typeAC.setAdapter(typeAdapter)

        rv_addSearc.layoutManager = LinearLayoutManager(this)

        btn_addItem.setOnClickListener{
            addIngredience()
        }
        btn_addAll.setOnClickListener{
            addAll()
        }
        btn_advadnceSerch.setOnClickListener {
            val where = getWhere()
            where + getWhereSur()
            callActivity(where)
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null){
            hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }
//    fun resize

    private fun addIngredience(){
        val name = at_AddSurovinaAV.text.toString().trim()
        hideKeyboard(this)
        if (name.isNotEmpty()){
            val addOK = data_search.any { it.name == name }
            if (!addOK) {
                id_inger = id_inger.inc()
                pridatIngredienci(name)
                data_search.add(SQLdata.Suroviny(id_inger,name,""))
                val addId = DBHelper(this).sellectOneIDIngredience(name)
                data_id.add(addId)
                at_AddSurovinaAV.text.clear()
                showIngred()

            }else{
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Surovina se již nachází ve vyhledavani")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }
    }

    private fun pridatIngredienci(name: String) {
            val DB = DBHelper(this)
            val add = DB.selectINGREDIENCE(name)
            if (add.isEmpty()){
                DB.insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
            }
    }


    private fun showIngred(){
        val adapter = SurovinyAdapter(this, data_search)
        rv_addSearc.adapter = adapter
    }

    fun deleteSurovinu(sData: SQLdata.Suroviny){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat ${sData.name}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("ANO") { dialogInterface, which ->
            val addId = DBHelper(this).sellectOneIDIngredience(sData.name)
            data_id.remove(addId)
            data_search.remove(sData)
            showIngred()

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun addAll(){
        val spis = DBHelper(this).selectSpiz()
        for (item in spis){
            val addOK = data_search.any { it.name == item.name }
            if (!addOK){
                id_inger = id_inger.inc()
                data_search.add(SQLdata.Suroviny(id_inger,item.name,""))
                val addId = DBHelper(this).sellectOneIDIngredience(item.name)
                data_id.add(addId)
            }
        }
        showIngred()
    }
    fun getWhere(): String{
        var where = ""
        val typ = ac_typeAV.text.toString()
        val category = ac_categoryAV.text.toString()
        if (typ.isNotEmpty()){
            where = where + " ( RECEPT.TYPE = '$typ' ) "
        }
        if (category.isNotEmpty()){
            if (where.length > 1) {
                where = where + " ( RECEPT.CATEGORY ='$category'  )"
            }else{
                where = where + "AND ( RECEPT.CATEGORY ='$category'  )"
            }
        }
//        val type = ac_type.text.toString()
//        val category = ac_category.
        if (data_id.isNotEmpty()){
            val sur = getWhereSur()
            if (where.length < 2){
                where = where + " " + sur
            }else{
                where = where + " AND " + sur
            }
        }
//        val sur = " AND " + getWhereSur()
        return where
    }
    fun getWhereSur(): String{
        var where : String = " SUROVINY_RECEPT.INGREDIENCE_ID IN ("
        var first = true
        for (i in data_id){
            if (first){
                first = false
                where = where + i
                continue
            }
            where = where +","+ i
        }
        where = where+" )"
        return where
    }

    fun callActivity(where:String){
        var text = where
        if (where.isEmpty()){
            text = " RECEPT.ID <> 0 "
        }
        var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
        arraySearched = DBHelper(this@AdvanceActivity).selectTitleIMG(text)
        if (arraySearched.isNotEmpty()) {
            Intent(this@AdvanceActivity, SearchedActivity::class.java).also {
                it.putExtra("EXTRA_SEARCHED", arraySearched)
                it.putExtra("EXTRA_TITLE", "")
                startActivity(it)
            }
        }else{
            Toast.makeText(applicationContext, "vybraná kriteria neodpovidají žádnému receptu", Toast.LENGTH_LONG).show()
        }
    }

}