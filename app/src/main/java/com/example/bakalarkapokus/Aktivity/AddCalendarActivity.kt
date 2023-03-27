package com.example.bakalarkapokus.Aktivity

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.activity_add_calendar.*
import kotlinx.android.synthetic.main.activity_add_recept.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCalendarActivity : AppCompatActivity() {
    val c = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_calendar)
        val gv_datum    = intent.getStringExtra("EXTRA_DATE")
        val gv_id       = intent.getIntExtra("EXTRA_ID",0)

        val gv_title = intent.getStringExtra("EXTRA_TITLE")

        if (gv_id != 0){
            set_item(gv_id)
        }
        if (gv_title != null){
            ac_Search_caladd.setText(gv_title)
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

//        ac_Search_caladd.addTextChangedListener(object : TextWatcher{
//                override fun afterTextChanged(s: Editable) {}
//
//                override fun beforeTextChanged(s: CharSequence, start: Int,
//                                               count: Int, after: Int) {
//        }
//
//                override fun onTextChanged(s: CharSequence, start: Int,
//                                           before: Int, count: Int) {
//            tvSample.setText("Text in EditText : "+s)
//        }

        //         AutoCoplitetextView
        val autoTextView : AutoCompleteTextView = findViewById(R.id.ac_Search_caladd)
        var listOfRecepts= DBHelper(this).selectAllTitles()
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfRecepts)
        autoTextView.threshold=1
        autoTextView.setAdapter(Autoadapter)
    }

    private fun set_item(id : Int){
        val DB = DBHelper(this)
        val calendar = DB.selectCalendarID(id)
        val recept : ArrayList<SQLdata.Recept>
        if (calendar != null){
            recept = DB.selectRECEPT(id)
            val year = calendar[0].year.toString()
            val month = calendar[0].month.toString()
            val day = calendar[0].day.toString()
            val string = "$year-$month-$day"
            setday(string)
            if (recept != null){
                ac_type_caladd.setText(calendar[0].type)
                ac_Search_caladd.setText(recept[0].title)
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
        if (type.isEmpty()){
            ac_type_caladd.setError("Povinné")
            check = true
        }else if (recept.isEmpty()){
            ac_Search_caladd.setError("Povinné")
            check = true
        }
        if (check){
            Toast.makeText(applicationContext, "Nezadali jste povinné hodnoty", Toast.LENGTH_LONG).show()
            return
        }
        //            Zjistit zdali uživatle zadal jen jeden recept
        val arraySearched = DBHelper(this).selectByTitle(recept)
        if (arraySearched.size != 1){
            Toast.makeText(applicationContext, "Vyberte jeden recept", Toast.LENGTH_LONG).show()
            return
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Přidat záznam do jídelníčku")
        builder.setMessage("Opravdu si přejete přidat záznam do Jídelníčku ")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            addCalendarSQL(arraySearched[0].id)}
        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun addCalendarSQL(recept_id : Int) {
        val DB = DBHelper(this)
        val type = ac_type_caladd.text.toString().trim()
        var year = c.get(Calendar.YEAR)
        var month  = c.get(Calendar.MONTH)
        var day    = c.get(Calendar.DAY_OF_MONTH)
        val status = DB.insertCalendar(SQLdata.Calendar(0,year, month, day, recept_id, type))
        if (status > 0){
            Toast.makeText(applicationContext, "Receprt přidám do jídelníčku", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(applicationContext,"Recept nepřidán",Toast.LENGTH_LONG).show()
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


}