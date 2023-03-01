package com.example.bakalarkapokus.Aktivity

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
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

class AddCalendarActivity : AppCompatActivity() {
    val c = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_calendar)
        val gv_datum = intent.getStringExtra("EXTRA_DATE")

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
        }else{
            addCalendarSQL(arraySearched[0].id)
        }
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