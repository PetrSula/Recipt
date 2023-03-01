package com.example.bakalarkapokus.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.CategoryAdapter
import com.example.bakalarkapokus.Adaptery.SearchAdapter
import com.example.bakalarkapokus.Aktivity.ReceptActivity
import com.example.bakalarkapokus.Aktivity.SearchedActivity
import com.example.bakalarkapokus.Aktivity.data_id
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dropdown_item.*
import kotlinx.android.synthetic.main.fragment_day.*
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_date = view.findViewById(R.id.tv_dayfrag_day)


        var iv_plus = view.findViewById<ImageView>(R.id.Iv_dayfrag_next)
        var iv_minus = view.findViewById<ImageView>(R.id.Iv_dayfrag_prew)
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
    }

    private fun getRecepies(year: Int, month: Int, day : Int) {
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val arrayRecepies = DB.selectCalendarDay(SQLdata.date(year,month, day))
        var arraySnidane = ArrayList<Int>()
        var arrayObed = ArrayList<Int>()
        var arraySvacina = ArrayList<Int>()
        var arrayVecere = ArrayList<Int>()
        for ( i in arrayRecepies){
            when (i.type){
                "Snídaně" -> { arraySnidane.add(i.recept_id)}
                "Oběd" -> { arrayObed.add(i.recept_id)}
                "Svačina" -> { arraySvacina.add(i.recept_id)}
                "Večeře" -> { arrayVecere.add(i.recept_id)}
            }
        }
        rv_Snidane(arraySnidane)
        rv_Obed(arrayObed)
        rv_Svacina(arraySvacina)
        rv_Vecere(arrayVecere)

    }

    fun rv_Snidane(arrayList : ArrayList<Int>){

        val activity = getContext()
        val DB = DBHelper(activity!!)
        val where = getWhere(arrayList)
        recepies_Snidane = DB.selectTitleIMG(where)

        rv_dayfrag_snidane.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = SearchAdapter(activity!!,recepies_Snidane)
        rv_dayfrag_snidane.adapter = Adapter
        Adapter.setOnItemClickListener(object : SearchAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                return
            }
        })
    }
    fun rv_Obed(arrayList : ArrayList<Int>){
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val where = getWhere(arrayList)
        val recepies = DB.selectTitleIMG(where)

        rv_dayfrag_obed.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = SearchAdapter(activity!!,recepies)
        rv_dayfrag_obed.adapter = Adapter
    }
    fun rv_Svacina(arrayList : ArrayList<Int>){
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val where = getWhere(arrayList)
        val recepies = DB.selectTitleIMG(where)

        rv_dayfrag_svacina.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = SearchAdapter(activity!!,recepies)
        rv_dayfrag_svacina.adapter = Adapter
    }
    fun rv_Vecere(arrayList : ArrayList<Int>){
        val activity = getContext()
        val DB = DBHelper(activity!!)
        val where = getWhere(arrayList)
        val recepies = DB.selectTitleIMG(where)

        rv_dayfrag_vecere.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = SearchAdapter(activity!!,recepies)
        rv_dayfrag_vecere.adapter = Adapter

        Adapter.setOnItemClickListener(object : SearchAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
            return
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
    private fun getWhere(list: ArrayList<Int>):String{
        if (list.isEmpty()){
            var where = "AND RECEPT.ID = 0"
            return where
        }
        var where : String = "AND ("
        var first = true
        for (i in list){
            if (first){
                first = false
                where = where + "RECEPT.ID = " + i
                continue
            }
            where = where + " or RECEPT.ID = " + i
        }
        where = where+")"
        return where
    }

    private fun clearRV(){
        recepies_Snidane.clear()
        recepies_Obed.clear()
        recepies_Svacina.clear()
        recepies_Vecere.clear()
        rv_dayfrag_obed?.adapter?.notifyDataSetChanged()
        rv_dayfrag_svacina?.adapter?.notifyDataSetChanged()
        rv_dayfrag_vecere?.adapter?.notifyDataSetChanged()
    }
}

