package com.example.bakalarkapokus

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.ItemAdapter
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.spiz.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.ingredience_main.*
import android.content.DialogInterface




class DruhaAktivita :AppCompatActivity(){

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spiz)

        println("Do something!")

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
        //AutoCompleteTeextView
        //ArrayAdapter<>
        val autoTextView : AutoCompleteTextView = findViewById(R.id.at_pridatsurovinu)
        val neco = getIngrediences()
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,neco)
        autoTextView.threshold=1
        autoTextView.setAdapter(adapter)


        getIngrediences()

        // Lister pro Přidat záznam surovina
        btn_AddSpiz.setOnClickListener{
            val name = at_pridatsurovinu.text.toString().trim()
            val addOK = DBHelper(this).selectItemSPIZ(name)
            if (addOK.isEmpty()){
                pridatZaznam()
            }else{
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Ingredience je již ve Spíži")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }

        }
        // volání funkce pro naplnění RecicleView
        setupListofDataIntoRecyclerView()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListofDataIntoRecyclerView(){

        if (getItems().size > 0){
            rv_Spiz.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            rv_Spiz.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this,getItems())
            rv_Spiz.adapter = itemAdapter

        } else{
            rv_Spiz.visibility = GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    fun pridatZaznam(){
        val name = at_pridatsurovinu.text.toString().trim()
        val db= DBHelper(this)
        if (name.isNotEmpty()) {
            pridatIngredienci()
            val Ingredience: ArrayList<SQLdata.Ingredience>
            Ingredience = db.selectINGREDIENCE(name)
            val status = db.insertDataSpiz(SQLdata.Spiz(0,Ingredience[0].id, name))
            if (status > 1) {
                Toast.makeText(applicationContext, "Přidáno", Toast.LENGTH_LONG).show()
                at_pridatsurovinu.text.clear()

                setupListofDataIntoRecyclerView()
            }
        }
        else{
            Toast.makeText(applicationContext,"Něco se pokazilo",Toast.LENGTH_LONG).show()
        }
    }

    fun pridatIngredienci(){
        val name = at_pridatsurovinu.text.toString().trim()
        val DB = DBHelper(this)
        val add = DB.selectINGREDIENCE(name)
        if (add.isEmpty()){
            val status = DB.insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
            if (status >1){
            }
        }
    }

    private fun getItems(): ArrayList<SQLdata.Spiz>{

        val db = DBHelper(this)

        return db.selectSpiz()
    }

    fun deleteRecord(spiz: SQLdata.Spiz){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat ${spiz.name}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("ANO"){
            dialogInterface,which ->

            val DB = DBHelper(this)
            val status = DB.deleteSpiz(SQLdata.Spiz(spiz.id,0,""))
            if (status > -1){
                Toast.makeText(
                    applicationContext,
                    "Záznam vymazán",
                    Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("NE"){
            dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun updateRecord(spiz: SQLdata.Spiz){
        val updateDialog = Dialog(this,R.style.Theme_Dialog)
        updateDialog.setCancelable(true)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.etUpdateName.setText(spiz.name)

        updateDialog.tvUpdate.setOnClickListener(View.OnClickListener {
            val name = updateDialog.etUpdateName.text.toString()

            val DB = DBHelper(this)

            if (name.isNotEmpty()){
                val status = DB.updateSpiz(SQLdata.Spiz(spiz.id,0,name))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Record Upraven.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()
                }
            }else{
                Toast.makeText(
                    applicationContext,"Něco se nepovedlo", Toast.LENGTH_LONG).show()
            }

        })
        updateDialog.tvCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        updateDialog.show()
    }

    fun getIngrediences():ArrayList<String>{
        var listOfIngredience = DBHelper(this).selectallIngredience()
        return listOfIngredience
    }


}