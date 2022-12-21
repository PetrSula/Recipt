package com.example.bakalarkapokus.Aktivity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.bakalarkapokus.Adaptery.surovinyAdapter
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.RealPathUtil
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.activity_add_recept.*
import kotlinx.android.synthetic.main.dialog_img.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


/* ToDo  - default obrázek
         - není upraveno přístup/oprava obrázku
         - vymazat surovinu při editaci
    BUG
         - Přidání suroviny do Spíze, prázdné, červené zobrazení
         - zkontrolovat nahrávání surovin testovat
         - uvozovky při nahrávání
         - přejmenovat exportovaný soubor
         - co když je nahrán prázdný soubor nebo jiný formát
*/
var data = ArrayList<SQLdata.Suroviny>()
var data_del = ArrayList<SQLdata.Suroviny>()
var id_addSurovin = 0
var gv_id = 0


class AddRecept: AppCompatActivity() {
    private var img_Path: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        data.clear()
        gv_id = intent.getIntExtra("EXTRA_ID",0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recept)

        iv_add_dish_image.setOnClickListener{
            customImageSelectionDialog()
        }
        iv_more.setOnClickListener{
            set_portion(true)
        }
        iv_less.setOnClickListener{
            set_portion(false)
        }
        btn_addSurivina.setOnClickListener{
            addSurovina()
        }
        btn_add_dish.setOnClickListener{
            if (gv_id != 0){
                updateRecept(gv_id )
            }else{
                addRecept()
            }
        }
        et_cooking_time.setOnClickListener {
            SnapTimePickerDialog.Builder().apply {
                setTitle(R.string.TimeTitle)
                setPrefix(R.string.time_prefix)
                setThemeColor(R.color.primary)
                setTitleColor(R.color.white)
            }.build().apply {
                setListener {
                        hour, minute ->
                    onTimePicked(hour, minute)
                }
            }.show(
                supportFragmentManager,
                SnapTimePickerDialog.TAG
            )
        }

        if (gv_id != 0){
            btn_add_Upload.visibility = View.GONE
        }
//      pokud přístup z editace ukaž recept
        showRecept(gv_id)

        val autoTextView : AutoCompleteTextView = findViewById(R.id.at_AddSurovina)
        var listOfIngredience = DBHelper(this).selectallIngredience()
        // REciclewiew
        val Autoadapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listOfIngredience)
        autoTextView.threshold=1
        autoTextView.setAdapter(Autoadapter)

        rv_addSuroviny.layoutManager = LinearLayoutManager(this)

        val adapter = surovinyAdapter(this, data)
        rv_addSuroviny.adapter = adapter
// ADaptery
        setAdaptercategory()
        adapterQuantity(" ")
        adaptertype()

        btn_add_Upload.setOnClickListener {
            importCSV()
        }

    }

    private fun setAdaptercategory() {
        val stringCategory = resources.getStringArray(R.array.categoryRecept)
        val categoryAdapter = ArrayAdapter(this,R.layout.dropdown_item,stringCategory)
        val categoryAC = findViewById<AutoCompleteTextView>(R.id.ac_category)
        categoryAC.setAdapter(categoryAdapter)
    }

//    private fun setAdapterQuantity() {
//        val stringTypeQuantity = resources.getStringArray(R.array.quantity)
//        val quantityAdapter = ArrayAdapter(this,R.layout.dropdown_item, stringTypeQuantity)
//        val quantityAC = findViewById<AutoCompleteTextView>(R.id.ac_typequantity)
//        quantityAC.setAdapter(quantityAdapter)
//    }


    fun showRecept(id: Int){
        val DB = DBHelper(this)
        val recept :ArrayList<SQLdata.Recept> = DB.selectRECEPT(id)
        if (recept.isEmpty()){
            return
        }
        val data_edit = recept.get(0)
//        val file = File(data_edit.img)
//        var imgURI = Uri.fromFile(file)
        val title = data_edit.title
        val postup = data_edit.postup
        val type = data_edit.type
        val category = data_edit.category
        val portion = data_edit.portion.toString()
        val sur = getSuroviny(id)
        loadDataFromAsset(data_edit.img)
        img_Path = data_edit.img
        for (item in sur){
            data.add(SQLdata.Suroviny(item.id,item.name,item.quantity))
        }

        et_title.setText(title)
        ac_type.setText(type)
        ac_category.setText(category)
        et_cooking_time.setText(data_edit.time)
        tv_portion.text = portion
        et_direction_to_cook.setText(postup)

    }
    fun loadDataFromAsset(path : String){
        var Image: ImageView
            Image = findViewById(R.id.iv_dish_image)
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
        val requestCode=RequestOptions.placeholderOf(R.drawable.ic_add)
        Glide.with(this)
            .load(R.drawable.ic_edit)
            .apply(requestCode)
            .into(iv_add_dish_image)
    }

    fun adapterQuantity(default : String){
        val stringTypeQuantity = resources.getStringArray(R.array.quantity)
        val quantityAdapter = ArrayAdapter(this,R.layout.dropdown_item, stringTypeQuantity)
        val quantityAC = findViewById<AutoCompleteTextView>(R.id.ac_typequantity)
//        val position = quantityAC.getPosition(" ")
//        quantityAC.setText(typeAdapter.getItem(position))
        quantityAC.setText(default)
        quantityAC.setAdapter(quantityAdapter)
    }

    fun adaptertype(){
        //        adapter pro dropdown Typ
        val stringType = resources.getStringArray(R.array.typeOfRecept)
        val typeAdapter = ArrayAdapter(this, R.layout.dropdown_item, stringType)
        val typeAC = findViewById<AutoCompleteTextView>(R.id.ac_type)
        typeAC.setAdapter(typeAdapter)
    }

    fun getSuroviny(id: Int):ArrayList<SQLdata.RvSurovinyRecept>{
        val DB = DBHelper(this)
        var suroviny:ArrayList<SQLdata.RvSurovinyRecept> = DB.selectSUROVINYrecept(id)
        return suroviny
    }

    fun onTimePicked(selectedHour: Int, selectedMinute: Int) {
        val hour = selectedHour.toString()
            .padStart(2, '0')
        val minute = selectedMinute.toString()
            .padStart(2, '0')
        var the_time = et_cooking_time.text.toString().trim()
        the_time  = String.format(
            getString(
                R.string.selected_time_format,
                hour, minute))
        et_cooking_time.setText(the_time)

    }

    fun editSurovinaRV(sData: SQLdata.Suroviny){
        var qunatity = sData.quantity.split(" ")
            .dropLast(1)
            .joinToString(" ")
        var type = sData.quantity.substring(sData.quantity.lastIndexOf(' '))
        at_AddSurovina.setText(sData.name)
        et_quantyti.setText(qunatity.trim())
        ac_typequantity.setText(type)
        SVadd.scrollToDescendant(et_direction_to_cook)
        adapterQuantity(type)
    }

    fun set_portion(boolean: Boolean){
        var value = tv_portion.text.toString()
        var number = value.toInt()
        if (boolean){
            number += 1
            value = number.toString()
            tv_portion.setText(value)
        }
        else{
            number -= 1
            if (number < 0){
                number = 0
            }
            value = number.toString()
            tv_portion.setText(value)
        }
    }


    private fun customImageSelectionDialog() {
        val dialog = Dialog(this,R.style.Theme_Dialog)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_img)
        dialog.tvcamera.setOnClickListener{
            checkCameraPermission()
            dialog.dismiss()
        }
        dialog.tvgallery.setOnClickListener {
            checkStoragePermission()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkCameraPermission() {
        if (hasPermission(true)) {
            // Have permission, do the thing!
            val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (galleryIntent.resolveActivity(packageManager) != null) { // its always null
                startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                REQUEST_CODE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    }

    private fun checkStoragePermission() {
        if (hasPermission(false)) {
            // Have permission, do the thing!
            val takePictureIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (takePictureIntent.resolveActivity(packageManager) != null) { // its always null
                startActivityForResult(takePictureIntent, REQUEST_FILE_GALLERY)
            }

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    }

    private fun hasPermission(camera:Boolean): Boolean {
        if (camera){
            return EasyPermissions.hasPermissions(
                this,
                Manifest.permission.CAMERA
            ) && EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }else{
            return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
            data?.extras?.let {
                val pic: Bitmap = data.extras!!.get("data") as Bitmap
                val requestOptions = RequestOptions.centerCropTransform()

                Glide.with(this)
                    .load(pic)
                    .apply(requestOptions)
                    .into(iv_dish_image)
                img_Path = saveIMG(pic)

                Glide.with(this)
                    .load(R.drawable.ic_edit)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_add))
                    .into(iv_add_dish_image)
            }
        }
        if (requestCode == REQUEST_FILE_GALLERY) {
            data?.let {
                val vybrane_photo = data.data
                val requestOptions = RequestOptions.centerCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(this)
                    .load(vybrane_photo)
                    .apply { requestCode }
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "Error loading image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                val bitmap: Bitmap = resource.toBitmap()
                                img_Path = saveIMG(bitmap)
                            }
                            return false
                        }
                    })
                    .into(iv_dish_image)

                val requestCode = RequestOptions.placeholderOf(R.drawable.ic_add)

                Glide.with(this)
                    .load(R.drawable.ic_edit)
                    .apply(requestCode)
                    .into(iv_add_dish_image)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FILE_DOWL && data != null){
            val uri = data?.data
            if (uri != null){
                val path = RealPathUtil.getRealPath(this,uri)
                var inputStream = this.contentResolver.openInputStream(uri)
                val buffer = inputStream?.bufferedReader(charset("Cp1250"))
                var line = buffer?.readLine()
                var line2 = buffer?.readLine()
                if (line2 != null ){
                        translateCSV(line2)
                }else if (line != null ){
                    translateCSV(line)
                }else {
                    Toast.makeText(this,"Soubor se nepovedlo nahrát zkontrolujte formát",Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun addSurovina () {
//        val spinner: Spinner = (findViewById(R.id.ac_typequantity))
        val typeQuantity = ac_typequantity.text.toString().trim()
        var name = at_AddSurovina.text.toString().trim()
        val quantyti = et_quantyti.text.toString().trim()
//        val typeQuantity: String = spinner.selectedItem.toString()
        var final_quaintity = quantyti  + ' ' + typeQuantity
        hideKeyboard(this)

        if (name.isNotEmpty()){
            val addOK = data.any { it.name == name }
            if (!addOK) {
                pridatIngredien(name)
                id_addSurovin = id_addSurovin.inc()
                data.add(SQLdata.Suroviny(id_addSurovin,name,final_quaintity))
                showSuroviny()
            }else{
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Surovina se již nachází v receptu, přejete si ji změnit")
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                alertDialog.setPositiveButton("ANO"){ dialogInterface, which->
                    var index = data.indexOfFirst{ it.name == name}
                    var item = data.get(index)
                    data.set(index,SQLdata.Suroviny(item.id,name,final_quaintity))
                    dialogInterface.dismiss()
                    showSuroviny()
                }
                alertDialog.setNegativeButton("NE"){ dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                val alert: AlertDialog = alertDialog.create()
                alert.setCancelable(true)
                alert.show()
            }
            ac_typequantity.text.clear()
            at_AddSurovina.text.clear()
            et_quantyti.text.clear()
        }else{
            Toast.makeText(applicationContext, "Název suroviny nevyplněn", Toast.LENGTH_LONG).show()
        }
    }


    private fun showSuroviny(){
        val adapter = surovinyAdapter(this, data)
        rv_addSuroviny.adapter = adapter
    }

    fun deleteSurovinu(sData: SQLdata.Suroviny){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vymazat záznam")
        builder.setMessage("Opravdu si přejete vymazat ${sData.name}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("ANO") { dialogInterface, which ->
            if (gv_id != 0){
                data_del.add(sData)
            }
            data.remove(sData)
            showSuroviny()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("NE") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
    fun deleteSurDB(){
        val DB = DBHelper(this)
        for (sData in data_del) {
            var del: ArrayList<SQLdata.Ingredience> = DB.selectINGREDIENCE(sData.name)
            val ingredienceID = del[0].id
            DB.deleteSurRecept(ingredienceID, gv_id)
        }
    }
    fun pridatIngredien(name: String ) {
//        val name = at_pridatsurovinu.text.toString().trim()
        val DB = DBHelper(this)
        val add = DB.selectINGREDIENCE(name)
        if (add.isEmpty()){
            val status = DB.insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
            if (status >1){
            }
        }
    }

    fun addRecept(){
        val title = et_title.text.toString().trim()
        val type = ac_type.text.toString().trim()
        val category = ac_category.text.toString().trim()
        val time = et_cooking_time.text.toString().trim()
        val postup = et_direction_to_cook.text.toString()
        val typeQuantity = ac_typequantity.text.toString().trim()
        val strQuantity = et_quantyti.text.toString().trim()
        val quantity = strQuantity  + ' ' + typeQuantity
        val strPortion = tv_portion.text.toString()
        val portion = strPortion.toInt()
        val img = img_Path
        var chech = true
        val DB = DBHelper(this)
        til_title.setError(null)
        tvAddSurovina.setError(null)
        til_direction_to_cook.setError(null)
        et_cooking_time.setError(null)

        if (title.isEmpty()) {
            Toast.makeText(applicationContext, "Název receptu nevyplněn", Toast.LENGTH_LONG).show()
            til_title.setError("Povinné")
            chech = false
        } else if(data.isEmpty()){
            Toast.makeText(applicationContext, "Není vyplněna ani jedna surovina", Toast.LENGTH_LONG).show()
            tvAddSurovina.setError("Povinné")
            chech = false
        } else if (postup.isEmpty()) {
            Toast.makeText(applicationContext, "Postup receptu nevyplněn", Toast.LENGTH_LONG).show()
            Toast.makeText(applicationContext, "Postup receptu nevyplněn", Toast.LENGTH_LONG).show()
            til_direction_to_cook.setError("Povinné")
            chech = false
        }else if (type.isEmpty()){
                Toast.makeText(applicationContext, "Druh receptu nevyplněn", Toast.LENGTH_LONG).show()
                ac_type.setError("Povinné")
                chech = false
        }else if (category.isEmpty()){
            Toast.makeText(applicationContext, "Kategorie receptu nevyplněna", Toast.LENGTH_LONG).show()
            ac_category.setError("Povinné")
            chech = false
        } else if (time.isEmpty()){
            Toast.makeText(applicationContext, "Čas přípravy receptu nevyplněn", Toast.LENGTH_LONG).show()
            et_cooking_time.setError("Povinné")
            chech = false
        }
        if (chech){
            val status = DB.insertRECEPT(SQLdata.Recept(0,title, type,category,time, postup, quantity, portion ,img))
            //insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
            if (status > 0){
                Toast.makeText(applicationContext, "Přidáno", Toast.LENGTH_LONG).show()
                for ( i in data) {
                    var add: ArrayList<SQLdata.Ingredience> = DB.selectINGREDIENCE(i.name)
                   // add = DB.selectINGREDIENCE(i.name)
                    val ingredienceID = add[0].id
                    val receptID = status.toInt()
                    val quantyti = i.quantity
                    DB.insertSURIVONYrecept(SQLdata.SurovinyRecept(0,ingredienceID,receptID,quantyti))
                }
                displayRecept(status.toInt())
            }else{
                Toast.makeText(applicationContext,"Recept nepřidán",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateRecept(id : Int){
        val title = et_title.text.toString().trim()
        val type = ac_type.text.toString().trim()
        val category = ac_category.text.toString().trim()
        val time = et_cooking_time.text.toString().trim()
        val postup = et_direction_to_cook.text.toString()
        val typeQuantity = ac_typequantity.text.toString().trim()
        val strQuantity = et_quantyti.text.toString().trim()
        val quantity = strQuantity  + ' ' + typeQuantity
        val strPortion = tv_portion.text.toString()
        val portion = strPortion.toInt()
        val img = img_Path
        var chech = true
        val DB = DBHelper(this)
        til_title.setError(null)
        tvAddSurovina.setError(null)
        til_direction_to_cook.setError(null)
        et_cooking_time.setError(null)

        if (title.isEmpty()) {
            Toast.makeText(applicationContext, "Název receptu nevyplněn", Toast.LENGTH_LONG).show()
            til_title.setError("Povinné")
            chech = false
        } else if(data.isEmpty()){
            Toast.makeText(applicationContext, "Není vyplněna ani jedna surovina", Toast.LENGTH_LONG).show()
            tvAddSurovina.setError("Povinné")
            chech = false
        } else if (postup.isEmpty()){
            Toast.makeText(applicationContext, "Postup receptu nevyplněn", Toast.LENGTH_LONG).show()
            til_direction_to_cook.setError("Povinné")
            chech = false
        }else if (time.isEmpty()){
            Toast.makeText(applicationContext, "Čas přípravy receptu nevyplněn", Toast.LENGTH_LONG).show()
            et_cooking_time.setError("Povinné")
            chech = false
        }else if (type.isEmpty()){
            Toast.makeText(applicationContext, "Druh receptu nevyplněn", Toast.LENGTH_LONG).show()
            ac_type.setError("Povinné")
            chech = false
        }else if (category.isEmpty()){
            Toast.makeText(applicationContext, "Kategorie receptu nevyplněna", Toast.LENGTH_LONG).show()
            ac_category.setError("Povinné")
            chech = false}
        if (chech){
            val status = DB.updateRecept(SQLdata.Recept(id,title, type,category,time,postup,quantity,portion,img))
            updateSurovinyRecept(id)
            displayRecept(id)
        }
    }
    fun updateSurovinyRecept(receptID : Int){
        deleteSurDB()
//        adapterQuantity()
        val DB = DBHelper(this)
        for (item in data){
            val itemDB = DB.selectOneSurRecept(item.id)
            if (item.name != itemDB.name){
                var add: ArrayList<SQLdata.Ingredience> = DB.selectINGREDIENCE(item.name)
                val ingredienceID = add[0].id
                val quantyti = item.quantity
                DB.insertSURIVONYrecept(SQLdata.SurovinyRecept(0,ingredienceID,receptID,quantyti))
            }else if (item.quantity != itemDB.quantity) {
                var add: ArrayList<SQLdata.Ingredience> = DB.selectINGREDIENCE(item.name)
                val ingredienceID = add[0].id
                val quantyti = item.quantity
                DB.updateSur(SQLdata.SurovinyRecept(item.id, ingredienceID, receptID, quantyti))
            }
        }
    }

    fun pridatIngredienci(name : String){
        val DB = DBHelper(this)
        val add = DB.selectINGREDIENCE(name)
        if (add.isEmpty()){
            DB.insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
        }
    }

    fun displayRecept(id:Int){
        Intent(this,ReceptActivita::class.java).also {
            it.putExtra("EXTRA_ID",id)
            if (gv_id == 0){
                finish()
            }else{
                ReceptActivita.Myclass.activity?.finish()
                finish()
            }
            startActivity(it)

        }
    }

    fun saveIMG(bitmap: Bitmap): String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun importCSV() {
        val path : String
        val builder = AlertDialog.Builder(this)
        builder.setTitle(" Vzor náhrávání souboru CSV")
        builder.setMessage("Pro nahrání receptu je nezbytné dodržet následující formát.\n\n" +
                "Suroviny jako celek musí být od ostatních položek rozděleny \"\" a čárkou viz vzor recept. Jednotlivé suroviny oddělte čárkami a název s množstvím |\n\n" +
                "RECEPT: \n"+
                "\"Název\", \"Druh\", \"Kategorie\", \"Suroviny\", \"Čas\", \"Porce\", \"Postup\"\n\n" +
                "SUROVINA: \n" +
                "nazev | množství,\n\n" +
                "Čas zadávejte pouze číselně ve formátu HH:MM")
        builder.setPositiveButton("ROZUMÍM") { dialogInterface, which ->
            executeImport()
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
    fun executeImport(){
        if (hasPermission(false)) {
//            val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*")
            startActivityForResult(Intent.createChooser(intent,"Vybrat"), REQUEST_FILE_DOWL)
//            intent.setType("*/*")
//            startActivityForResult(Intent.createChooser(intent,"VYBRAT SOUBOR"),1)
        }else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_camera),
                REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
        var neco : Uri = Uri.EMPTY
        var file = File(getRealPathFromURI(neco))

    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun translateCSV(line : String){
        val list : List<String> = line.split("\",")
        try {
            val title = list.get(0).replace("\"", "").trim()
            et_title.setText(title)
        }catch (e: Exception) {
            return
        }
        try{
            val druh = list.get(1)
            setDruh(druh)
        }catch (e: Exception) {
            return
        }
        try {
            val category = list.get(2)
            setCategory(category)
        }catch (e: Exception) {
            return
        }
        try {
            val sur = list.get(3)
            setSuroviny(sur)
        }catch (e: Exception) {
            return
        }
        try {
            val sur = list.get(3)
            setSuroviny(sur)
        }catch (e: Exception) {
            return
        }
        try {
            val cas = list.get(4)
            setCas(cas)
        }catch (e: Exception) {
            return
        }
        try {
            val porce = list.get(5)
            setPorce(porce)
        }catch (e: Exception) {
            return
        }
        try {
            val postup = list.get(6)
            setPostup(postup)
        }catch (e: Exception) {
            return
        }

    }

    private fun setCas(string: String) {
        var output = string.replace("\"", "")
        val cas = output.split(":")
        if (cas.size == 2) {
            val hod = cas.get(0)
            val min = cas.get(1)
            val numH = hod.filter { it.isDigit() }
            val numM = hod.filter { it.isDigit() }
            if (numH.isNotEmpty() && numM.isNotEmpty()) {
                et_cooking_time.setText(output)
            }
        }
    }

    private fun setPorce(string: String) {
        val porce = string.filter { it.isDigit() }
        if (porce.isNotEmpty()){
            tv_portion.setText(porce)
        }
    }

    private fun setPostup(string: String) {
        var postup = string.replace("\"", "")
        et_direction_to_cook.setText(postup)
    }

    private fun setSuroviny(sur: String) {
        data.clear()
        val input = sur.replace("\"", "")
        val listSur = input.split(",")
        for ( i in listSur){
            val item = i.split("|")
            if (item.size != 2){
                continue
            }
            val name = item[0].trim()
            var quantyti = item[1].trim()
            var numberQuantity = quantyti.filter { it.isDigit() }
            var typeQunatity = quantyti.filter { it.isLetter() }
            val number = name.filter { it.isDigit() }

            if (number.isNotEmpty() || name.isEmpty()){
                continue
            }else{
                pridatIngredienci(name)
                val addQuantity = numberQuantity + " " + typeQunatity
                data.add(SQLdata.Suroviny(id_addSurovin,name,addQuantity))
                id_addSurovin = id_addSurovin.inc()
            }
//            if (size == 0){
//                return
//            }else if (size == 1){
//                pridatIngredien(item[0])
//                data.add(SQLdata.Suroviny(id_addSurovin,item[0],""))
//            }
        }
        showSuroviny()
    }

    private fun setDruh(string: String) {
        var druh = string.replace("\"", "")
        druh = druh.capitalize().trim()
        val array = resources.getStringArray(R.array.typeOfRecept)
        if (druh in array) {
            ac_type.setText(druh)
        }
        adaptertype()
    }

    private fun setCategory(string: String) {
        var category = string.replace("\"", "")
        category = category.capitalize().trim()
        val array = resources.getStringArray(R.array.categoryRecept)
        if (category in array) {
            ac_category.setText(category)
        }
        //        adapter pro dropdown Kategorie
        setAdaptercategory()
    }


    companion object {
        private const val REQUEST_FILE_DOWL = 223
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_FILE_GALLERY = 2
        private const val REQUEST_CODE: Int = 123
        private const val IMAGE_DIRECTORY = "RecipesImg"
    }







}