package com.example.bakalarkapokus

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
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
import java.io.InputStream
import java.util.Arrays.fill
import java.util.Collections.fill

/* TODO - obrázek check on
        - Přepočítávání porcí
        - budík čas přípravy
        - upravit postup

 */

class ReceptActivita: AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    class Myclass{
        companion object{
            var activity: Activity? = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recept)
        Myclass.activity = this@ReceptActivita

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
//                R.id.miItem2 -> {
//                    val intent = Intent(this, ReceptActivita::class.java)
//                    startActivity(intent)
//                    true
//                }
                R.id.miItem0 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("EXIT",true)
                    true
                }
                else -> false
            }
        }
        loadDataFromAsset("pictures/baseMeal.webp")
        showRecept(gv_id)
        iv_edit_recept.setOnClickListener{
            editaceRecept(gv_id)
        }
        btn_rec_spotreb.setOnClickListener {
            spotrevSur(gv_id)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    fun loadDataFromAsset(path : String){
        var Image: ImageView
        Image = findViewById(R.id.iv_rec_Picture)
        val check : Boolean = "pictures/" in path
        if (check) {
            try {
                var ims = getAssets().open(path)
                var drawable = Drawable.createFromStream(ims, null)
                Image.setImageDrawable(drawable)
            } catch (e: Exception) {
                return
            }
        }else{
            val file = File(path)
            var imgURI = Uri.fromFile(file)
            Glide.with(this)
                .load(imgURI)
                .into(Image)
        }
    }

    fun showRecept(id: Int){
        val DB = DBHelper(this)
        val recept :ArrayList<SQLdata.Recept> = DB.selectRECEPT(id)
        val data = recept.get(0)
        val file = File(data.img)
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
        loadDataFromAsset(data.img)


//        Glide.with(this)
//            .load(imgURI)
//            .into(ivPicture)

        shouwSuroREcept(id)

        tvTitleRecept.text = title
        tvPostuprm.text = postup
        tvType.text = type
        tvCategory.text = category
        tv_portionR.text = portion
        tvTimeminutes.text = time

    }
    fun shouwSuroREcept(id: Int){
        val sur = getSuroviny(id)

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
    }
    fun setTime(list: List<String>) : String{
        var hour = list[0]
        var minutes = list[1] + " min"
        var test = hour[0].toString()
        if (hour.equals("00")){
            hour = ""
        }
        else if(test.equals("0")){
            hour = hour[1].toString() + " h"
        }
        else{hour = hour + " h"
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
    fun spotrevSur(id:Int){
        val spis= DBHelper(this).selectSpiz()
        val builder = AlertDialog.Builder(this)
        val sur = getSuroviny(id)
        var showSur = ArrayList<SQLdata.Ingredience>()
        var i = 0
        for (item in sur){
            val addOK = spis.any { it.name == item.name }
            if (addOK) {
                showSur.add(SQLdata.Ingredience(i,item.name))
                i += 1
            }
        }
        var show = Array<String>(showSur.size){" it = $it"}
        for (item in showSur){
            show.set(item.id,item.name)
        }
        var checkedItems = BooleanArray(show.size)
        val selectedItems = ArrayList<Int>()
        builder.setTitle("Vyberte suroviny ke spotřebování")
        if (checkedItems.isEmpty()){
            builder.setMessage("Recept nepoužívá žádné suroviny ze spíže")
        }else {
            builder.setMultiChoiceItems(
                show,
                checkedItems,
                DialogInterface.OnMultiChoiceClickListener() { dialogInterface, which, isChecked ->
                    if (isChecked) {
                        selectedItems.add(which)
                    } else if (selectedItems.contains(which)) {
                        selectedItems.remove(which)
                    }
                })
            builder.setPositiveButton("ANO") { dialogInterface, which ->
                odebratSpiz(selectedItems, showSur)
                shouwSuroREcept(id)
                dialogInterface.dismiss()
            }
            builder.setNegativeButton("NE") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
        }
//        builder.setNeutralButton("Vybrat všechny",null)
//        {
//                dialogInterface, which ->
//            checkedItems.all { it-> false }
//        }
        builder.setCancelable(true)
        builder.create().show()
//        val neutralBtn = builder.getButton(AlertDialog.BUTTON_NEUTRAL)
//        builder.
    }
    fun odebratSpiz(surID:ArrayList<Int>, surSpiz: ArrayList<SQLdata.Ingredience>){
        for (item in surID) {
            val name = surSpiz.get(item)
            DBHelper(this).deleteSpizByName(name.name)
        }
        return
    }
}