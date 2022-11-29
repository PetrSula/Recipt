package com.example.bakalarkapokus

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Recept.surovinyAdapter
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_vyhledavani.*
import kotlinx.android.synthetic.main.recept_postup.*

val data_search = ArrayList<SQLdata.Suroviny>()
var id_inger = 0

class AdvanceActivity : AppCompatActivity(){
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vyhledavani)
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
        val categoryAdapter = ArrayAdapter(this,R.layout.dropdown_item,stringCategory)
        val categoryAC = findViewById<AutoCompleteTextView>(R.id.ac_category)
        categoryAC.setAdapter(categoryAdapter)
//        adapter pro dropdown Typ
        val stringType = resources.getStringArray(R.array.typeOfRecept)
        val typeAdapter = ArrayAdapter(this, R.layout.dropdown_item, stringType)
        val typeAC = findViewById<AutoCompleteTextView>(R.id.ac_type)
        typeAC.setAdapter(typeAdapter)

        rv_addSearc.layoutManager = LinearLayoutManager(this)

        btn_addItem.setOnClickListener{
            addIngredience()
        }
        btn_addAll.setOnClickListener{
            addAll()
        }

    }
    private fun addIngredience(){
        val name = at_AddSurovinaAV.text.toString().trim()
        hideKeyboard(this)
        if (name.isNotEmpty()){
            val addOK = data_search.any { it.name == name }
            if (!addOK) {
                id_inger = id_inger.inc()
                data_search.add(SQLdata.Suroviny(id_inger,name,""))
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

    private fun showIngred(){
        val adapter = surovinyAdapter(this, data_search)
        rv_addSearc.adapter = adapter
    }

    fun deleteSurovinu(sData: SQLdata.Suroviny){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat ${sData.name}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("ANO") { dialogInterface, which ->
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
            }
        }
        showIngred()
    }
}