package com.example.bakalarkapokus.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakalarkapokus.Adaptery.SearchAdapter
import com.example.bakalarkapokus.Adaptery.weekAdapter
import com.example.bakalarkapokus.Aktivity.*
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_week.*
import java.text.DateFormat
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val c = Calendar.getInstance()
var fabVisible = false


class WeekFragment (string: String) : Fragment(R.layout.fragment_week) {

    val gv_date = string
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        set_dates()
        getRecepies()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        Iv_weekfrag_next.setOnClickListener {
            change_date(true) }
        Iv_weekfrag_prew.setOnClickListener {
            change_date(false)        }
        iv_weekfrag_cal.setOnClickListener {
            val activity = getContext()
            val pickerDialog = DatePickerDialog(
                activity!!,
                { view, year, monthOfYear, dayOfMonth ->
                    c.set(year,monthOfYear,dayOfMonth)
                    set_dates()
                    getRecepies()
                },
                year,
                month,
                day
            )
            pickerDialog.show()
        }


        fb_week_Add.setOnClickListener {
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val date = year.toString() + "-" + month.toString() + "-" + day.toString()
            val activity = getContext()
            Intent(activity, AddCalendarActivity::class.java).also {
                it.putExtra("EXTRA_DATE", date)
                startActivity(it)
            }
            }
        }


    fun set_dates(){
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(c.time)
        c.firstDayOfWeek = Calendar.MONDAY
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday = dateFormat.format(c.time)
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = dateFormat.format(c.time)
        tv_weekfrag_frsday.text = monday
        tv_weekfrag_lstday.text = sunday
    }

    fun change_date(plus : Boolean){
        if (plus){
            c.add(Calendar.DATE,7)
            set_dates()
            getRecepies()
        }else{
            c.add(Calendar.DATE,-7)
            set_dates()
            getRecepies()
        }
    }
    private fun getRecepies() {
        c.firstDayOfWeek = Calendar.MONDAY
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var recepies : ArrayList<SQLdata.Calendar>
        for (i in 1 .. 7){
            if (i != 1){
                c.add(Calendar.DATE,1)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val arrayRecepies = DB.selectCalendarDay(SQLdata.date(year,month, day))
            when (i){
                1 -> Monday(arrayRecepies)
                2 -> Tuesday(arrayRecepies)
                3 -> Wednesday(arrayRecepies)
                4 -> Thursday(arrayRecepies)
                5 -> Friday(arrayRecepies)
                6 -> Saturday(arrayRecepies)
                7 -> Sunday(arrayRecepies)
            }
        }

    }

    private fun getWhere(recept_id : Int) : String{
        var where  = " RECEPT.ID = $recept_id "
        return where
    }

    fun Monday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_po.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_po.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Tuesday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_ut.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_ut.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Wednesday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_st.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_st.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Thursday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_ct.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_ct.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Friday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_pa.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_pa.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Saturday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_so.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_so.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun Sunday(recepies : ArrayList<SQLdata.Calendar>){
        var dataAdapter = ArrayList<SQLdata.Week>()
        val activity = getContext()
        val DB = DBHelper(activity!!)
        var title : String
        for (i in recepies){
            val where = getWhere(i.recept_id)
            val recept = DB.selectTitleIMG(where)
            title = recept[0].title
            if (title.length > 30){
                title = title.substring(0, 30) + "..."
            }
            dataAdapter.add(SQLdata.Week(i.id,title,recept[0].img,i.type))
        }
        rv_weekfrag_ne.layoutManager = LinearLayoutManager(activity!!)
        var Adapter = weekAdapter(activity!!,dataAdapter,"WeekFragment")
        rv_weekfrag_ne.adapter = Adapter

        Adapter.setOnItemClickListener(object : weekAdapter.onItemClickListener {

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
    fun del_get_permision(row: SQLdata.Calendar){
        val activity = getContext()
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat záznam ze dne ${row.day}.${row.month+1}.${row.year}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            deleteRow(row.id)
            getRecepies()
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