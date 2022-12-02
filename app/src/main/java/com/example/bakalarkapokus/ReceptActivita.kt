package com.example.bakalarkapokus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bakalarkapokus.Recept.ReceptAdapter
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.navView
import kotlinx.android.synthetic.main.spiz.*
import kotlinx.android.synthetic.main.activity_recept.*
import kotlinx.android.synthetic.main.recept_postup.*
import java.io.File

/* TODO - obrázek check on
        - Přepočítávání porcí
        - budík čas přípravy
        - upravit postup

 */

class ReceptActivita: AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recept)

        val gv_id = intent.getIntExtra("EXTRA_ID",1)
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
        showRecept(gv_id)
        iv_edit_recept.setOnClickListener{
            editaceRecept(gv_id)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    fun showRecept(id: Int){
        val DB = DBHelper(this)
        val recept :ArrayList<SQLdata.Recept> = DB.selectRECEPT(id)
        val data = recept.get(0)
        val file = File(data.img)
        var imgURI = Uri.fromFile(file)
        val title = data.title
        val postup = data.postup
        val type = data.type
//        val postup = "Maso poprašte mletou paprikou a vmasírujte ji do něj ze všech stran. Poté maso osolte a opepřete (asi 1/4 lžičkou od každého).\n" +
//                "Ve velké hluboké pánvi rozehřejte 1 lžíci másla na středním ohni. Maso na něm opékejte nejprve z jené strany asi 3 minuty, až bude hezky dozlatova opečené. Poté maso obraťte, ztlumte na střední plamen a opékejte zase asi 3 minuty.\n" +
//                "Poté do pánve přidejte zbylé máslo, tymián a česnek a za stálého míchání restujte asi 2 minuty.\n" +
//                "Zalijte vínem, přiveďte k mírnému varu a nechte vařit asi 20 minut. Víno se zredukuje, pokud je potřeba, klidně ještě víno přidejte, aby v pánvi zůstala nějaká omáčka.\n" +
//                "Na závěr přidejte do omáčky pokrájený špenát a nechte jen zavadnout. Dochuťte solí a pepřem, ujistěte se, že je maso propečené a podávejte s rýží nebo širokými nudlemi.\n" +
//                "Prima tip: Pokud chcete, můžete půlku másla nahradit olivovým olejem.
        var timeArray: List<String> = data.time.split(":")
        val time = setTime(timeArray)
        val category = data.category
        val portion = data.portion.toString()


        Glide.with(this)
            .load(imgURI)
            .into(ivPicture)

        val sur = getSuroviny(id)
//        získat poměř


        if (sur.isEmpty()){
            rv_recept.visibility = GONE
            tvNoItems.visibility = VISIBLE
        }
        else{
            rv_recept.visibility = VISIBLE
            tvNoItems.visibility = GONE
            rv_recept.layoutManager = LinearLayoutManager(this)
            val adapter = ReceptAdapter(this,sur)
            rv_recept.adapter = adapter
        }

        tvTitleRecept.text = title
        tvPostuprm.text = postup
        tvType.text = type
        tvCategory.text = category
        tv_portionR.text = portion
        tvTimeminutes.text = time

    }
    fun setTime(list: List<String>) : String{
        var hour = list[0]
        var minutes = list[1] + "min"
        var test = hour[0].toString()
        if (hour.equals("00")){
            hour = ""
        }
        else if(test.equals("0")){
            hour = hour[1].toString() + "h"
        }
        else{hour = hour + "h"
        }
        return hour + ' '+ minutes
    }
    fun getSuroviny(id: Int):ArrayList<SQLdata.RvSurovinyRecept>{
        val DB = DBHelper(this)
        var suroviny:ArrayList<SQLdata.RvSurovinyRecept> = DB.selectSUROVINYrecept(id)
        return suroviny
    }
    fun editaceRecept(id: Int){
        Intent(this,AddRecept::class.java).also {
            it.putExtra("EXTRA_ID", id)
            startActivity(it)
        }
    }
}