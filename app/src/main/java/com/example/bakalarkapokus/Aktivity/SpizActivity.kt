package com.example.bakalarkapokus.Aktivity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Adaptery.SpizAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_spiz.*
import kotlinx.android.synthetic.main.dialog_update.*



class SpizActivity :AppCompatActivity(){

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spiz)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val autoTextView : AutoCompleteTextView = findViewById(R.id.at_pridatsurovinu)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close )
        drawerLayout.addDrawerListener(toggle)
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
                    val where = " "
                    var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                    arraySearched = DBHelper(this@SpizActivity).selectTitleIMG(where)
                    Intent(this@SpizActivity,SearchedActivity::class.java).also {
                        it.putExtra("EXTRA_SEARCHED", arraySearched)
                        startActivity(it)
                    }
                    true
                }
                R.id.miItem3 ->{
                    finish()
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.miItem0 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("EXIT",true)
                    startActivity(intent)
                    true
                }R.id.miItem4 ->{
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                true
            }
                else -> false
            }
        }
        //AutoCompleteTeextView


//        ArrayAdapter.createFromResource(
//            this,R.array.quantity,android.R.layout.simple_spinner_item).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        }
        //ArrayAdapter<>

        val neco = getIngrediences()
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,neco)
        autoTextView.threshold=1
        autoTextView.setAdapter(adapter)




        getIngrediences()

        // Lister pro Přidat záznam surovina
        btn_AddSpiz.setOnClickListener{
            addSuroSpiz()
            hideKeyboard(this)
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

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)

    }
    private fun addSuroSpiz(){
        val name = at_pridatsurovinu.text.toString().trim()
        if (name.isNotEmpty()) {
            // Naplnění SPINNERu
//            val typeQuantity: String = spinner.selectedItem.toString()
//            val quantity = at_pridatmnozstvi.text.toString().trim()
            val addOK = DBHelper(this).selectItemSPIZ(name)
//            val final_quaintity = quantity  + ' ' + typeQuantity
//            closeKeyboard(at_pridatmnozstvi)
            if (addOK.isEmpty()) {
                pridatZaznam()
            } else {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Ingredience je již ve Spíži")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }else{
            Toast.makeText(applicationContext,"Nezadali jste žádnou surovinu",Toast.LENGTH_LONG).show()
            at_pridatsurovinu.setError("Povinné")
        }
    }

    private fun setupListofDataIntoRecyclerView(){

        if (getItems().size > 0){
            rv_Spiz.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            rv_Spiz.layoutManager = LinearLayoutManager(this)
            val itemAdapter = SpizAdapter(this,getItems())
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
            pridatIngredienci(name)
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

    fun pridatIngredienci(name: String ) {
//        val name = at_pridatsurovinu.text.toString().trim()
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
        builder.setMessage("Opravdu si přejete záznam vymazat ${spiz.name}")
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
                    Toast.makeText(applicationContext, "Záznam upraven.", Toast.LENGTH_LONG).show()

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