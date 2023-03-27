package com.example.bakalarkapokus.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.CategoryAdapter
import com.example.bakalarkapokus.Adaptery.SearchAdapter
import com.example.bakalarkapokus.Adaptery.weekAdapter
import com.example.bakalarkapokus.Aktivity.*
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dropdown_item.*
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_week.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class DayFragment : Fragment(R.layout.fragment_day) {
    val c = Calendar.getInstance()
    lateinit var tv_date: TextView
    lateinit var recepies_Snidane: ArrayList<SQLdata.AraySearched>
    lateinit var recepies_Obed: ArrayList<SQLdata.AraySearched>
    lateinit var recepies_Svacina: ArrayList<SQLdata.AraySearched>
    lateinit var recepies_Vecere: ArrayList<SQLdata.AraySearched>
    var fabVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_date = view.findViewById(R.id.tv_dayfrag_day)

//      Change date
        var iv_plus = view.findViewById<ImageView>(R.id.Iv_dayfrag_next)
        var iv_minus = view.findViewById<ImageView>(R.id.Iv_dayfrag_prew)
//      Set floting button
        fb_day.setOnClickListener {
            showmenu(fabVisible)
        }
//        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val date = year.toString() + "-" + month.toString() + "-" + day.toString()
//        var dayOfWeek = sdf.format(date)
//        var dayOfWeek = getWeekDayName(date)

        val calendar = Calendar.getInstance()
        val time = c.time
        val dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(time)
        tv_date.text = dateInfo
//        získat všechyn recepty na daný den
        getRecepies(year, month, day)

//        val Monday = DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.getDefault())
//        tv_date.text = Monday
        iv_plus.setOnClickListener {
            change_date(view, true)
        }
        iv_minus.setOnClickListener {
            change_date(view, false)
        }

        tv_date.setOnClickListener {
            val activity = getContext()
            val pickerDialog = DatePickerDialog(
                activity!!,
                { view, year, monthOfYear, dayOfMonth ->
                    tv_date.text =
                        (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                },
                year,
                month,
                day
            )
            pickerDialog.show()
        }

        fb_day_Add.setOnClickListener {
            val activity = getContext()
            val intent = Intent(activity, AddCalendarActivity::class.java)
            startActivity(intent)
        }
    }
    fun showmenu(visible : Boolean){
        if (!visible){
            fb_day_Add.show()
            fb_day_Edit.show()
            fb_day_Edit.visibility = View.VISIBLE
            fb_day_Add.visibility = View.VISIBLE
            fabVisible = true
        }else{

            fb_day_Add.hide()
            fb_day_Edit.hide()
            fb_day_Edit.visibility = View.GONE
            fb_day_Add.visibility = View.GONE
            fabVisible = false
        }
    }

    private fun getRecepies(year: Int, month: Int, day : Int) {
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val arrayRecepies = DB.selectCalendarDay(SQLdata.date(year,month, day)) // SGData.Calendar
        var arraySnidane = ArrayList<SQLdata.Calendar>()
        var arrayObed = ArrayList<SQLdata.Calendar>()
        var arraySvacina = ArrayList<SQLdata.Calendar>()
        var arrayVecere = ArrayList<SQLdata.Calendar>()
        for ( i in arrayRecepies){
            when (i.type){
                "Snídaně" -> { arraySnidane.add(i)}
                "Oběd" -> { arrayObed.add(i)}
                "Svačina" -> { arraySvacina.add(i)}
                "Večeře" -> { arrayVecere.add(i)}
            }
        }
        rv_Snidane(arraySnidane)
        rv_Obed(arrayObed)
        rv_Svacina(arraySvacina)
        rv_Vecere(arrayVecere)

    }

    fun rv_Snidane(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            dataAdapter.add(SQLdata.Week(i.id,recept[0].title,recept[0].img,i.type))
        }

        rv_dayfrag_snidane.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"DayFragment")
        rv_dayfrag_snidane.adapter = Adapter
        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener  {

            override fun onItemClick(position: Int) {
                Intent(activity, ReceptActivity::class.java).also {
                    val calndarRow = dataAdapter[position]
                    if (calndarRow == null){
                        return
                    }
                    val row = recepies.find { it.id == calndarRow.id }
                    it.putExtra("EXTRA_ID", row?.recept_id)
                    startActivity(it)
                }
            }

            override fun imageDelClick(position: Int) {
                super.imageDelClick(position)
                val row = recepies.find { it.id == position }
                if (row != null) {
                    del_get_permision(row)
                }
            }

            override fun imageEditClick(position: Int) {
                super.imageEditClick(position)
                Intent(activity, AddCalendarActivity::class.java).also {
                    val row = recepies.find { it.id == position }
                    it.putExtra("EXTRA_ID", position)
                    startActivity(it)
                }
            }
        })
    }

    fun rv_Obed(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            dataAdapter.add(SQLdata.Week(i.id,recept[0].title,recept[0].img,i.type))
        }

        rv_dayfrag_obed.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"DayFragment")
        rv_dayfrag_obed.adapter = Adapter
        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener  {

            override fun onItemClick(position: Int) {
                Intent(activity, ReceptActivity::class.java).also {
                    val calndarRow = dataAdapter[position]
                    if (calndarRow == null){
                        return
                    }
                    val row = recepies.find { it.id == calndarRow.id }
                    it.putExtra("EXTRA_ID", row?.recept_id)
                    startActivity(it)
                }
            }

            override fun imageDelClick(position: Int) {
                super.imageDelClick(position)
                val row = recepies.find { it.id == position }
                if (row != null) {
                    del_get_permision(row)
                }
            }

            override fun imageEditClick(position: Int) {
                super.imageEditClick(position)
                Intent(activity, AddCalendarActivity::class.java).also {
                    val row = recepies.find { it.id == position }
                    it.putExtra("EXTRA_ID", position)
                    startActivity(it)
                }
            }
        })
    }

    fun rv_Svacina(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            dataAdapter.add(SQLdata.Week(i.id,recept[0].title,recept[0].img,i.type))
        }

        rv_dayfrag_svacina.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"DayFragment")
        rv_dayfrag_svacina.adapter = Adapter
        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener  {

            override fun onItemClick(position: Int) {
                Intent(activity, ReceptActivity::class.java).also {
                    val calndarRow = dataAdapter[position]
                    if (calndarRow == null){
                        return
                    }
                    val row = recepies.find { it.id == calndarRow.id }
                    it.putExtra("EXTRA_ID", row?.recept_id)
                    startActivity(it)
                }
            }

            override fun imageDelClick(position: Int) {
                super.imageDelClick(position)
                val row = recepies.find { it.id == position }
                if (row != null) {
                    del_get_permision(row)
                }
            }

            override fun imageEditClick(position: Int) {
                super.imageEditClick(position)
                Intent(activity, AddCalendarActivity::class.java).also {
                    val row = recepies.find { it.id == position }
                    it.putExtra("EXTRA_ID", position)
                    startActivity(it)
                }
            }
        })
    }

    fun rv_Vecere(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            dataAdapter.add(SQLdata.Week(i.id,recept[0].title,recept[0].img,i.type))
        }

        rv_dayfrag_vecere.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"DayFragment")
        rv_dayfrag_vecere.adapter = Adapter
        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener  {

            override fun onItemClick(position: Int) {
                Intent(activity, ReceptActivity::class.java).also {
                    val calndarRow = dataAdapter[position]
                    if (calndarRow == null){
                        return
                    }
                    val row = recepies.find { it.id == calndarRow.id }
                    it.putExtra("EXTRA_ID", row?.recept_id)
                    startActivity(it)
                }
            }

            override fun imageDelClick(position: Int) {
                super.imageDelClick(position)
                val row = recepies.find { it.id == position }
                if (row != null) {
                    del_get_permision(row)
                }
            }

            override fun imageEditClick(position: Int) {
                super.imageEditClick(position)
                Intent(activity, AddCalendarActivity::class.java).also {
                    val row = recepies.find { it.id == position }
                    it.putExtra("EXTRA_ID", position)
                    startActivity(it)
                }
            }
        })
    }

    private fun change_date(view: View,plus: Boolean){
        tv_date = view.findViewById(R.id.tv_dayfrag_day)
        if (plus){
            c.add(Calendar.DATE,1)
            val time = c.time
            val dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(time)
            tv_date.text = dateInfo
            getRecepies( c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }else{
            c.add(Calendar.DATE,-1)
            val time = c.time
            val dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(time)
            tv_date.text = dateInfo
            getRecepies( c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }
        return
    }
    private fun getWhere(recept_id : Int):String{
//        if (list.isEmpty()){
//            var where = "AND RECEPT.ID = 0"
//            return where
//        }
//        var where : String = "AND ("
//        var first = true
//        for (i in list){
//            if (first){
//                first = false
//                where = where + "RECEPT.ID = " + i
//                continue
//            }
//            where = where + " or RECEPT.ID = " + i
//        }
//        where = where+")"
//        return where
        var where  = "AND RECEPT.ID = $recept_id "
        return where
    }

    fun del_get_permision(row: SQLdata.Calendar){
        val activity = getContext()
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat záznam ze dne ${row.day}.${row.month+1}.${row.year}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            deleteRow(row.id)
            getRecepies(c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun deleteRow(id:Int){
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val succes = DB.deleteCalendarDay(id)
        if (succes != 0){
            Toast.makeText(activity!!, "Záznam vymazán", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(activity!!, "Nepodařilo se vymazat záznam", Toast.LENGTH_LONG).show()
        }
    }
}

