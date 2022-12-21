package com.example.bakalarkapokus.Aktivity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bakalarkapokus.Adaptery.ReceptAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
//import com.itextpdf.text.pdf.PdfDocument
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.navView
import kotlinx.android.synthetic.main.activity_recept.*
import java.io.*
import java.nio.charset.Charset
import kotlin.collections.ArrayList

/* TODO - obrázek check on
        - Přepočítávání porcí
        - budík čas přípravy
        - upravit postup

 */

class ReceptActivita: AppCompatActivity() {
    lateinit var  et_pdf_data :EditText
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var imgPath : String
    lateinit var gv_time : String

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
                    val intent = Intent(this, SpizAktivita::class.java)
                    startActivity(intent)
                    true
                }
                R.id.miItem2 -> {
                    val where = " "
                    var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
                    arraySearched = DBHelper(this@ReceptActivita).selectTitleIMG(where)
                    Intent(this@ReceptActivita,SearchedActivity::class.java).also {
                        it.putExtra("EXTRA_SEARCHED", arraySearched)
                        startActivity(it)
                    }
                    true
                }
                R.id.miItem0 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("EXIT",true)
                    true
                }
                R.id.miItem3 ->{
                    val intent = Intent(this, AdvanceActivity::class.java)
                    startActivity(intent)
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
        iv_recept_delete.setOnClickListener {
            deleteRecept(gv_id)
        }
        iv_recept_export.setOnClickListener {
            generateCSV(gv_id)
        }
        btn_rec_spotreb.setOnClickListener {
            spotrevSur(gv_id)
        }
    }

    private fun deleteRecept(id: Int) {
        val title = tvTitleRecept.text
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Vymazat recept $title")
        builder.setMessage("Opravdu si přejete vymazat Recept ")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            DBHelper(this).deleteRecept(id)
            DBHelper(this).deleteSurRecept_id(id)
            dialogInterface.dismiss()
            SearchedActivity.SearchableMyclass.activity?.finish()
            finish()
            val where = " "
            var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
            arraySearched = DBHelper(this@ReceptActivita).selectTitleIMG(where)
            Intent(this@ReceptActivita,SearchedActivity::class.java).also {
                it.putExtra("EXTRA_SEARCHED", arraySearched)
                startActivity(it)
            }
        }
        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun generateCSV(id: Int){
        val titelIN = tvTitleRecept.text.toString().trim()
        val druh = tvType.text.toString().trim()
        val kategorie = tvCategory.text.toString().trim()
        var postup = tvPostuprm.text.toString().trim()
        val cas = gv_time
        val suroviny = getSurovinyCSV(id)
        val portion = tv_portionR.text.toString()
        val postup2 = postup.replace("\n", " ").replace( "\r", "").replace("\t"," ")
//        val mFileName = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//            .format(System.currentTimeMillis())
        val fileName = titelIN
        val file: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  fileName + ".csv")
        try {
//            val fileOut =
            var fileWriter = OutputStreamWriter(FileOutputStream(file), Charset.forName("Cp1250"))
            var buffer = BufferedWriter(fileWriter)
            buffer.write("Název, Druh, Kategorie, Suroviny, Čas, Postup, Porce")
            buffer.newLine()
            buffer.write("\"$titelIN\", \"$druh\", \"$kategorie\",\"$suroviny\", \"$cas\",\"$portion\",\"$postup2\"")
            buffer.flush()
            buffer.close()
            Toast.makeText(applicationContext, "CSV soubor uložen do složky Stažené/Downloads", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(applicationContext, "Soubor se nepodařilo se vygenerovat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSurovinyCSV(id: Int): String {
        val sur  = getSuroviny(id)
        var suroviny : String = ""
        for (item in sur){
            suroviny = suroviny + "${item.name} | ${item.quantity}, "
        }
        return suroviny
    }

//    private fun preparePDF() {
//        val check : Boolean = "pictures/" in imgPath
//        if (check) {
//            try {
//                var ims = getAssets().open(imgPath)
//                bmp = BitmapFactory.decodeStream(ims)
//            } catch (e: Exception) {
//                return
//            }
//        }else{
//            bmp = BitmapFactory.decodeFile(imgPath)
//        }
//
////        bmp = BitmapFactory.decodeResource(resources, R.id.iv_rec_Picture)
//        scaledbmp = Bitmap.createScaledBitmap(bmp, 250, 250, false)
//        if (!checkPermissions()){
//            requestPermission()
//        }
//    }

//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    private fun generatePDF() {
//        var pdfDocument: PdfDocument = PdfDocument()
//        var paint: Paint = Paint()
//        var title: Paint = Paint()
//        var text: Paint = TextPaint()
//        var myPageInfo: PdfDocument.PageInfo? =
//            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
//        var canvas: Canvas = myPage.canvas
//        val titelIN = tvTitleRecept.text.toString().trim()
////        val postup = tvPostuprm.text.toString()
////        var layout : StaticLayout =
////        layout = ("1.Inventarizační kokpit UIK – \n zobrazené zůstatky jednotlivých účtů neodpovídají skutečným zůstatkům!!!",text, canvas.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
////        val postup =
//        canvas.drawBitmap(scaledbmp, 56F, 40F, paint)
//        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
//        title.textSize = 18F
//        title.setColor(ContextCompat.getColor(this, R.color.primary))
//        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
//        text.textSize = 15F
//        text.setColor(ContextCompat.getColor(this,R.color.secondary))
//        canvas.drawText(titelIN, 400F, 80F, title)
////        canvas.drawText(postup, 300F , 100F, text)
//        pdfDocument.finishPage(myPage)
//        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//            .format(System.currentTimeMillis())
////        var dowDir = Path.Combio
//        val file: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  mFileName + ".pdf")
////        val exFile = File(getExternalFilesDir("//sdcard//Download"),"name.pdf")
//
//        try {
////            val fileOutputStream = FileOutputStream
//            pdfDocument.writeTo(FileOutputStream(file))
//            Toast.makeText(applicationContext, "PDF uloženo do složky Stažené/Downloads", Toast.LENGTH_SHORT).show()
//            pdfDocument.close()
//        }catch (e: Exception){
//            e.printStackTrace()
//            Toast.makeText(applicationContext, "Nepodařilo se vygenerovat PDF", Toast.LENGTH_SHORT)
//                .show()
//        }
//
//    }

//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSUIN_CODE
//        )
//    }
//
//    private fun checkPermissions(): Boolean {
//        var writeStoragePermission = ContextCompat.checkSelfPermission(
//            applicationContext,
//            WRITE_EXTERNAL_STORAGE
//        )
//        var readStoragePermission = ContextCompat.checkSelfPermission(
//            applicationContext,
//            READ_EXTERNAL_STORAGE
//        )
//        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
//                && readStoragePermission == PackageManager.PERMISSION_GRANTED
//    }

    private fun savePDF() {
//        val mDoc = Document()
//        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//            .format(System.currentTimeMillis())
//        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"
//        try {
//            PdfWriter.getInstance(mDoc)
//
//        }catch (e: Exception){
//            Toast.makeText(this,""+e.toString(),Toast.LENGTH_SHORT).show()
//        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
//    nahrání obrázků pro recept
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
        gv_time = data.time
        var timeArray: List<String> = data.time.split(":")
        val time = setTime(timeArray)
        val category = data.category
        val portion = data.portion.toString()
        loadDataFromAsset(data.img)
        imgPath = data.img

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
            builder.setNegativeButton("Zpět") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
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
            builder.setPositiveButton("Spotřebovat") { dialogInterface, which ->
                odebratSpiz(selectedItems, showSur)
                shouwSuroREcept(id)
                dialogInterface.dismiss()
            }
            builder.setNegativeButton("Zpět") { dialogInterface, which ->
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

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSUIN_CODE){
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
//                == PackageManager.PERMISSION_GRANTED){
//
//            }
//        }
//    }
}