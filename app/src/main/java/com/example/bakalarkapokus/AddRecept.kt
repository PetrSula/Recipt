package com.example.bakalarkapokus

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.bakalarkapokus.Recept.surovinyAdapter
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.dialog_img.*
import kotlinx.android.synthetic.main.recept_postup.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList
import com.example.bakalarkapokus.DruhaAktivita as DruhaAktivita

val data = ArrayList<SQLdata.Suroviny>()
var id_addSurovin = 0


class AddRecept: AppCompatActivity() {
    private var img_Path: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recept_postup)

        iv_add_dish_image.setOnClickListener{
            customImageSelectionDialog()
        }
        btn_addSurivina.setOnClickListener{
            addSurovina()
        }

        btn_add_dish.setOnClickListener{
            addRecept()
        }

        rv_addSuroviny.layoutManager = LinearLayoutManager(this)

        val adapter = surovinyAdapter(this, data)
        rv_addSuroviny.adapter = adapter

    }

    val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, data)
    autoTextView.threshold=1
    autoTextView.setAdapter(adapter)


    private fun customImageSelectionDialog() {
        val dialog = Dialog(this,R.style.Theme_Dialog)
        dialog.setCancelable(false)
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
        if (hasPermission()) {
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
        if (hasPermission()) {
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

    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.CAMERA
        ) && EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE && data != null){
            data?.extras?.let {
                val pic: Bitmap = data.extras!!.get("data") as Bitmap
                val requestOptions = RequestOptions.centerCropTransform()

                Glide.with(this)
                    .load(pic)
                    .apply(requestOptions)
                    .into(iv_dish_image)
                img_Path= saveIMG(pic)

                Glide.with(this)
                    .load(R.drawable.ic_edit)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_add))
                    .into(iv_add_dish_image)
            }
        }
        if (requestCode == REQUEST_FILE_GALLERY){
            data?.let {
                val vybrane_photo = data.data
                val requestOptions = RequestOptions.centerCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(this)
                    .load(vybrane_photo)
                    .apply { requestCode }
                    .listener(object : RequestListener<Drawable>{
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

                val requestCode=RequestOptions.placeholderOf(R.drawable.ic_add)

                Glide.with(this)
                    .load(R.drawable.ic_edit)
                    .apply(requestCode)
                    .into(iv_add_dish_image)
            }
        }
    }

    private fun addSurovina () {
        val spinner: Spinner = (findViewById(R.id.sp_pridatmnozstvi))
        val name = et_AddSurovina.text.toString().trim()
        val quantyti = et_quantyti.text.toString().trim()
        val typeQuantity: String = spinner.selectedItem.toString()
        val final_quaintity = quantyti  + ' ' + typeQuantity
        hideKeyboard(this)
        if (name.isNotEmpty()){
            val addOK = data.any { it.name == name }
            if (!addOK) {
                pridatIngredien(name)
                id_addSurovin = id_addSurovin.inc()
                data.add(SQLdata.Suroviny(id_addSurovin,name,final_quaintity))
                et_AddSurovina.text.clear()
                et_quantyti.text.clear()
                //("přídat autocoplete ")
                showSuroviny()
            }else{
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Surovina se již nachází v receptu")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }

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
        val postup = et_direction_to_cook.text.toString()
        val img = img_Path
        var chech = true
        val DB = DBHelper(this)
        til_title.setError(null)
        tvAddSurovina.setError(null)
        til_direction_to_cook.setError(null)

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
        }
        if (chech){
            val status = DB.insertRECEPT(SQLdata.Recept(0,title,postup,img))
            //insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
            if (status >1){
                Toast.makeText(applicationContext, "Přidáno", Toast.LENGTH_LONG).show()
                for ( i in data) {
                    var add: ArrayList<SQLdata.Ingredience> = DB.selectINGREDIENCE(i.name)
                   // add = DB.selectINGREDIENCE(i.name)
                    val ingredienceID = add[0].id
                    val receptID = status.toInt()
                    val quantyti = i.quantity
                    DB.insertSURIVONYrecept(SQLdata.SurovinyRecept(0,ingredienceID,receptID,quantyti))
                }
            }else{
                Toast.makeText(applicationContext,"Něco se pokazilo",Toast.LENGTH_LONG).show()
            }
        }

        //("přidání ukládání surovin do tabulky ingredience")
        //("ošetřit přidání bez titulu, postupu či ingrediencí")
    }
    fun pridatIngredienci(name : String){
        val DB = DBHelper(this)
        val add = DB.selectINGREDIENCE(name)
        if (add.isEmpty()){
            DB.insertDataINGREDIENCE(SQLdata.Ingredience(0,name))
        }
    }
    fun pridatSurovinuRecept(name: String){

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
//    fun hideKeyboard() {
//        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        //Find the currently focused view, so we can grab the correct window token from it.
//        var view = this.currentFocus
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = View(this)
//        }
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
//    }


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_FILE_GALLERY = 2
        private const val REQUEST_CODE: Int = 123
        private const val IMAGE_DIRECTORY = "RecipesImg"
    }



}