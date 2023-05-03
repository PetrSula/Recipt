package com.example.bakalarkapokus.Aktivity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.SearchAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_add_calendar.*
import kotlinx.android.synthetic.main.activity_add_recept.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_search_advance.*
import kotlinx.android.synthetic.main.activity_searched.*
import java.security.AccessController.getContext
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCalendarActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    val c = Calendar.getInstance()
    var gv_id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_calendar)
        val gv_datum    = intent.getStringExtra("EXTRA_DATE")
        gv_id           = intent.getIntExtra("EXTRA_ID",0)
        val gv_title    = intent.getStringExtra("EXTRA_TITLE")
        val drawerLayout = findViewById<DrawerLayout>(R.id.DL_addcal)
        val navView     = findViewById<NavigationView>(R.id.navView_addcal)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> {
                    val intent = Intent(this, SpizActivity::class.java)
                    CalendarActivity.Myclass.activity?.finish()
                    finish()
                    startActivity(intent)
                    true
                }
                R.id.miItem2 -> {
                    val where = "  RECEPT.ID <> 0 "
                    var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                    arraySearched = DBHelper(this).selectTitleIMG(where)
                    Intent(this,SearchedActivity::class.java).also {
                        it.putExtra("EXTRA_SEARCHED", arraySearched)
                        CalendarActivity.Myclass.activity?.finish()
                        finish()
                        startActivity(it)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem0 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("EXIT",true)
                    CalendarActivity.Myclass.activity?.finish()
                    startActivity(intent)
                    finish()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.miItem3 ->{
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
                    CalendarActivity.Myclass.activity?.finish()
                    finish()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }R.id.miItem4 ->{
                val intent = Intent(this, CalendarActivity::class.java)
                CalendarActivity.Myclass.activity?.finish()
                finish()
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
                else -> false
            }
        }

        if (gv_id != 0){
            set_item(gv_id)
        }
        if (gv_title != null){
            ac_Search_caladd.setText(gv_title)
            search_handeler()
        }else if (ac_Search_caladd.text.toString() == null){
        tvNoRecordsAvailable.visibility = View.VISIBLE
        }
        setday(gv_datum)
        adaptertype()
        Iv_caladd_calendar.setOnClickListener {
            var datum = gv_datum
            if (datum == null){
                var year = c.get(Calendar.YEAR).toString()
                var month  = c.get(Calendar.MONTH).toString()
                var day    = c.get(Calendar.DAY_OF_MONTH).toString()
                datum = year + '-' + month + '-' + day
            }
            datepicker(datum)
        }
        bt_caladd.setOnClickListener {
            addCalendarCheck()
        }
        bt_cal_show.setOnClickListener{
            moveToCall()
        }
        ac_Search_caladd.setOnFocusChangeListener {  View,hasFocus ->
            if (!hasFocus){
                hideKeyboard(this)}
        }


        ac_Search_caladd.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search_handeler()
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
            }
        })

        //         AutoCoplitetextView
        val autoTextView : AutoCompleteTextView = findViewById(R.id.ac_Search_caladd)
        var listOfRecepts= DBHelper(this).selectAllTitles()
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfRecepts)
        autoTextView.threshold=1
//        autoTextView.setAdapter(Autoadapter)
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

    private fun moveToCall(){
        var year = c.get(Calendar.YEAR)
        var month  = c.get(Calendar.MONTH)
        var day    = c.get(Calendar.DAY_OF_MONTH)
        Intent(this,CalendarActivity::class.java).also {
            it.putExtra("EXTRA_DATE", "$year-$month-$day")
            CalendarActivity.Myclass.activity?.finish()
            finish()
            startActivity(it)
        }
    }

    private fun set_item(id : Int){
        val DB = DBHelper(this)
        val calendar = DB.selectCalendarID(id)
        val recept : ArrayList<SQLdata.Recept>
        if (calendar != null){
            recept = DB.selectRECEPT(calendar[0].recept_id)
            val year = calendar[0].year.toString()
            val month = calendar[0].month.toString()
            val day = calendar[0].day.toString()
            val string = "$year-$month-$day"
            setday(string)
            if (recept != null){
                ac_type_caladd.setText(calendar[0].type)
                ac_Search_caladd.setText(recept[0].title)
                search_handeler()
            }
        }

    }

    private fun setday(datum: String?) {
        var p_datum = datum
        var year : Int
        var month: Int
        var day : Int
        if (p_datum == null){
            year = c.get(Calendar.YEAR)
            month  = c.get(Calendar.MONTH)
            day    = c.get(Calendar.DAY_OF_MONTH)
        }else{
            var list :List<String> = p_datum.split("-")
            year = list[0].toInt()
            month = list[1].toInt()
            day   = list[2].toInt()
        }
        c.set(year,month,day)
        val time = c.time
        val dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(time)
        Tv_caladd_calendar.text = dateInfo
    }

    private fun addCalendarCheck() {
        var insert : SQLdata.Calendar
        var check = false
        val type = ac_type_caladd.text.toString().trim()
        val recept = ac_Search_caladd.text.toString().trim()
        ac_type_caladd.setError(null)
        ac_Search_caladd.setError(null)
        if (type.isEmpty()){
            ac_type_caladd.setError("Povinné")
            check = true
        }
        if (recept.isEmpty()){
            ac_Search_caladd.setError("Povinné")
            check = true
        }
        if (check){
            Toast.makeText(applicationContext, "Nezadali jste povinné hodnoty", Toast.LENGTH_LONG).show()
            return
        }
        //            Zjistit zdali uživatle zadal jen jeden recept
        val arraySearched = DBHelper(this).selectExactTitle(recept)
        if (arraySearched.size != 1){
            Toast.makeText(applicationContext, "Vyberte jeden recept", Toast.LENGTH_LONG).show()
            return
        }
        if (gv_id != 0){
            update_calendar(arraySearched[0].id)
        }else{
            add_calendar_bulder(arraySearched[0].id)
        }
    }

    private fun add_calendar_bulder(id : Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Přidat záznam do Jídelníčku")
        builder.setMessage("Opravdu si přejete přidat záznam do Jídelníčku ")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            CalendarSQL(id)}
        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
    private fun update_calendar(id: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upravit záznam v Jídelníčku")
        builder.setMessage("Opravdu si přejete upravit záznam v Jídelníčku ")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            CalendarSQL(id)}
        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun CalendarSQL(recept_id : Int) {
        val DB = DBHelper(this)
        val type = ac_type_caladd.text.toString().trim()
        var year = c.get(Calendar.YEAR)
        var month  = c.get(Calendar.MONTH)
        var day    = c.get(Calendar.DAY_OF_MONTH)
        if (gv_id !=0){
            val status = DB.updateCalendar(SQLdata.Calendar(gv_id,year, month, day, recept_id, type))
            if (status != null ){
                Intent(this,CalendarActivity::class.java).also {
                    it.putExtra("EXTRA_DATE","$year-$month-$day")
                    CalendarActivity.Myclass.activity?.finish()
                    finish()
                    startActivity(it)
                }
            }else{
                Toast.makeText(applicationContext,"Zázanam se nepovedlo upravit",Toast.LENGTH_LONG).show()
            }
        }else {
            val status = DB.insertCalendar(SQLdata.Calendar(0,year, month, day, recept_id, type))
            if (status > 0){
                Toast.makeText(applicationContext, "Receprt přidám do jídelníčku", Toast.LENGTH_LONG).show()
                ac_type_caladd.setText(" ")
                ac_Search_caladd.setText(" ")
                search_handeler()
                adaptertype()
            }else{
                Toast.makeText(applicationContext,"Recept nepřidán",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun adaptertype(){
        //        adapter pro dropdown Typ
        val list  = listOf("Snídaně", "Oběd", "Svačina", "Večeře")
        val array: Array<String> = list.toTypedArray()
        val typeAdapter = ArrayAdapter(this, R.layout.dropdown_item, array)
        val typeAC = findViewById<AutoCompleteTextView>(R.id.ac_type_caladd)
        typeAC.setAdapter(typeAdapter)
    }

    private fun datepicker(datum : String) {
        var list :List<String> = datum.split("-")
        var year = list[0].toInt()
        var month = list[1].toInt()
        var day   = list[2].toInt()

        var tv_date = findViewById<TextView>(R.id.Tv_caladd_calendar)
        val pickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                c.set(year,monthOfYear,dayOfMonth)
                val time = c.time
                val dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(time)
                tv_date.text = dateInfo
            },
            year,
            month,
            day
        )
        pickerDialog.show()
    }
//          upravuje zobrazování nabídek receptů dle zadaného textu uživatele
    private fun search_handeler() {
        rv_Sear_meat.visibility = View.GONE
        var arraySearched:ArrayList<SQLdata.AraySearched>
        val input = ac_Search_caladd.text.toString().trim()
        if (input.isEmpty()){
//            Toast.makeText(applicationContext, "Nezadali jste žádné hodnoty", Toast.LENGTH_LONG).show()
            return
        }
        arraySearched = DBHelper(this).selectByTitle(input)
        if (arraySearched.size != 0){
            tvNoRecordsAvailable.visibility = View.GONE
            setupListofDataIntoRecyclerView(arraySearched)
        }else{
            tvNoRecordsAvailable.visibility = View.VISIBLE
            return
        }
    }

    private fun setupListofDataIntoRecyclerView(array : ArrayList<SQLdata.AraySearched>){
        rv_Sear_meat.visibility = View.VISIBLE
        rv_Sear_meat.layoutManager = LinearLayoutManager(this)
        val itemAdapter = SearchAdapter(this,array)
        rv_Sear_meat.adapter = itemAdapter

        itemAdapter.setOnItemClickListener(object : SearchAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                hideKeyboard(this@AddCalendarActivity)
                val DB = DBHelper(this@AddCalendarActivity)
                val recept :ArrayList<SQLdata.Recept> = DB.selectRECEPT(array[position].id)
                ac_Search_caladd.setText(recept[0].title)
            }

        })
    }


}