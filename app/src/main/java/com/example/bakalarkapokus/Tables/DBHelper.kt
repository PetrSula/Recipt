package com.example.bakalarkapokus.Tables
/*TODO - zavrít databáze po každém dotazu
       - select DISTINCT RECEPT.ID from recept, SUROVINY_RECEPT
*/

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream


const val dbName = "MyDB.db"


class DBHelper (private val context: Context) :SQLiteOpenHelper(context, DATABASE_NAME,null,9) {
    private var dataBase: SQLiteDatabase? = null

    init {
        // Check if the database already copied to the device.
        val dbExist = checkDatabase()
        if (dbExist) {
            // if already copied then don't do anything.
            Log.e("-----", "Database exist")
        } else {
            // else copy the database to the device.
            Log.e("-----", "Database doesn't exist")
            createDatabase()
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val Create_table_SPIZ = "CREATE TABLE SPIZ (ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                                                    "NAME TEXT," +
                                                    "ID_INGREDIENCE INTEGER," +
                                                    "FOREIGN KEY (ID_INGREDIENCE) REFERENCES INGREDIENCE(ID))"
        val Create_table_RECEPT = "CREATE TABLE RECEPT (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                        "TITLE TEXT," +
                                                        "INGREDIENCE_ID INTEGER," +
                                                        "POSTUP TEXT," +
                                                        "IMG BLOB)"
        val Create_table_INGREDIENCE =" CREATE TABLE INGREDIENCE (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                    "NAME TEXT)"
        val Create_table_SUROVINY_RECEPT = "CREATE TABLE " + TABLE_SUROVINY_RECEPT +
                                                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                    INGREDIENCE_ID + " INTEGER,"+
                                                    RECEPT_ID + " INTEGER,"+
                                                    QUANTITY + " TEXT,"+
                                                    "FOREIGN KEY" + "(" + INGREDIENCE_ID + ")" + " REFERENCES "+ TABLE_INGREDIENCE +"(" + ID + "),"+
                                                    "FOREIGN KEY" + "(" + RECEPT_ID + ")" + " REFERENCES "+ TABLE_RECEPT +"(" + ID + ")"+ ")"
        db!!.execSQL(Create_table_SPIZ)
        db!!.execSQL(Create_table_INGREDIENCE)
        db!!.execSQL(Create_table_RECEPT)
        db.execSQL(Create_table_SUROVINY_RECEPT)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion == 3) {
            val Create_table_SPIZ = "CREATE TABLE SPIZ (ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "NAME TEXT," +
                    "ID_INGREDIENCE INTEGER," +
                    "FOREIGN KEY (ID_INGREDIENCE) REFERENCES INGREDIENCE(ID))"
            val Create_table_RECEPT = "CREATE TABLE RECEPT (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TITLE TEXT," +
                    "INGREDIENCE_ID INTEGER," +
                    "POSTUP TEXT," +
                    "IMG BLOB)"
            val Create_table_INGREDIENCE =
                " CREATE TABLE INGREDIENCE (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "NAME TEXT)"
            db!!.execSQL("DROP TABLE SPIZ")
            db!!.execSQL(Create_table_SPIZ)
            db!!.execSQL(Create_table_INGREDIENCE)
            db.execSQL(Create_table_RECEPT)
        }
        if (newVersion == 4){
            val drop_table_SPIZ = "DROP TABLE " + TABLE_SPIZ
            val drop_table_RECEPT = "DROP TABLE " + TABLE_RECEPT
            val Create_table_SPIZ = "CREATE TABLE " + TABLE_SPIZ + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                                        NAME+" TEXT,"+
                                                                        INGREDIENCE_ID+" INTEGER,"+
                                                                        QUANTITY + " TEXT,"+
                    "FOREIGN KEY" + "(" + INGREDIENCE_ID + ")" + " REFERENCES "+ TABLE_INGREDIENCE +"(" + ID + ")"+")"
            val Create_table_RECEPT = "CREATE TABLE " + TABLE_RECEPT + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                                        TITLE+" TEXT,"+
                                                                        POSTUP + " TEXT,"+
                                                                        IMG + " TEXT" +")"
            val Create_table_SUROVINY_RECEPT = "CREATE TABLE " + TABLE_SUROVINY_RECEPT +
                                                        "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                                    INGREDIENCE_ID + " INTEGER,"+
                                                                    RECEPT_ID + " INTEGER,"+
                                                                    QUANTITY + " TEXT,"+
                    "FOREIGN KEY" + "(" + INGREDIENCE_ID + ")" + " REFERENCES "+ TABLE_INGREDIENCE +"(" + ID + "),"+
                    "FOREIGN KEY" + "(" + RECEPT_ID + ")" + " REFERENCES "+ TABLE_RECEPT +"(" + ID + ")"+ ")"
            db!!.execSQL(drop_table_RECEPT)
            db!!.execSQL(drop_table_SPIZ)
            db!!.execSQL(Create_table_SPIZ)
            db!!.execSQL(Create_table_SUROVINY_RECEPT)
            db.execSQL(Create_table_RECEPT)
        }
        if (newVersion == 8){
            val drop_table_RECEPT = "DROP TABLE " + TABLE_RECEPT
            val Create_table_RECEPT = "CREATE TABLE " + TABLE_RECEPT + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                TITLE+" TEXT,"+
                                                TYPE +" TEXT," +
                                                CATEGORY + " TEXT," +
                                                TIME + " TEXT," +
                                                POSTUP + " TEXT,"+
                                                QUANTITY + " TEXT,"+
                                                PORTION + " INTEGER,"+
                                                IMG + " TEXT" +")"
            db!!.execSQL(drop_table_RECEPT)
            db.execSQL(Create_table_RECEPT)
        }
        if (newVersion == 9){
            val Create_table_CALENDAR =
                "CREATE TABLE $TABLE_CALENDAR ($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$YEAR INTEGER," +
                    "$MONTH INTEGER," +
                    "$DAY INTEGER," +
                        "$RECEPT_ID INTEGER," +
                        "$TYPE TEXT)"
            db!!.execSQL(Create_table_CALENDAR)
        }

    }
    private fun createDatabase() {
        copyDatabase()
    }
    private fun checkDatabase(): Boolean {
        val dbFile = File(context.getDatabasePath(dbName).path)
        return dbFile.exists()
    }
    private fun copyDatabase() {

        val inputStream = context.assets.open("$dbName")
        val outputFile = File(context.getDatabasePath(dbName).path)
        val outputStream = FileOutputStream(outputFile)

        val bytesCopied = inputStream.copyTo(outputStream)
        Log.e("bytesCopied", "$bytesCopied")
        inputStream.close()

        outputStream.flush()
        outputStream.close()
    }

    fun insertDataRecept(dataRecept: SQLdata.Recept):Long{
        val DB = this.writableDatabase
        val cv = ContentValues()

        val success = DB.insert(TABLE_SPIZ,null,cv)

        DB.close()
        return  success
    }

    fun insertDataSpiz(dataSpiz: SQLdata.Spiz): Long{
        val DB = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NAME,dataSpiz.name)
        contentValues.put("ID_INGREDIENCE",dataSpiz.id_Ingredience)

        val success = DB.insert(TABLE_SPIZ,null,contentValues)

        DB.close()
        return success


    }

    fun selectSpiz(): ArrayList<SQLdata.Spiz>{
        val spizList: ArrayList<SQLdata.Spiz> = ArrayList<SQLdata.Spiz>()
        val selecQuery = "SELECT * FROM SPIZ "
        val DB = this.readableDatabase
        var cursor: Cursor? = null

        try{
            cursor = DB.rawQuery(selecQuery,null)
        }
        catch (e:SQLiteException){
            DB.execSQL(selecQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var id_Ingredience: Int

        if (cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                id_Ingredience = cursor.getInt(cursor.getColumnIndexOrThrow("ID_INGREDIENCE"))

                val spiz = SQLdata.Spiz(id = id, id_Ingredience = id_Ingredience, name = name)
                spizList.add(spiz)

            }while (cursor.moveToNext())
        }
        return spizList
    }

    fun selectItemSPIZ(name: String): String{
        val selecQuery = "SELECT NAME FROM " + TABLE_SPIZ  +" where NAME =" + "'" + name + "'"
        val DB = this.readableDatabase
        var result :String = ""

        var cursor: Cursor? = null

        try{
            cursor = DB.rawQuery(selecQuery,null)
        }
        catch (e:SQLiteException){
            DB.execSQL(selecQuery)
            return String()
        }
        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
        }
        return result
    }

    fun deleteSpiz(dataSpiz: SQLdata.Spiz): Int{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID,dataSpiz.id)
        val success = DB.delete(TABLE_SPIZ, ID+"="+dataSpiz.id, null)
        DB.close()
        return success
    }

    fun deleteSpizByName(name: String){
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID,name)
        val success = DB.delete(TABLE_SPIZ, NAME+" = '$name'", null)
        DB.close()
    }

    fun updateSpiz(dataSpiz: SQLdata.Spiz):Int{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME,dataSpiz.name)

        val success = DB.update(TABLE_SPIZ,contentValues, ID + "=" + dataSpiz.id,null)
        DB.close()
        return success
    }

    fun insertDataINGREDIENCE(ingredience: SQLdata.Ingredience): Long{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME,ingredience.name)

        val success = DB.insert(TABLE_INGREDIENCE,null,contentValues)

        DB.close()
        return success
    }

    fun selectINGREDIENCE(name:String): ArrayList<SQLdata.Ingredience> {
        var where = ""
        if(name.isNotEmpty()){
            where = " WHERE NAME =" + "'" + name + "'"
        }
        val INGList: ArrayList<SQLdata.Ingredience> = ArrayList<SQLdata.Ingredience>()
        val selecQuery = "SELECT * FROM "+ TABLE_INGREDIENCE +where
        val DB = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        var id: Int
        var name: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                val data = SQLdata.Ingredience(id = id, name = name)
                INGList.add(data)
            } while (cursor.moveToNext())
        }
        return INGList
    }

    fun sellectOneIDIngredience(name:String) :Int{
        var id = -1
        val DB = this.readableDatabase
        val selecQuery = "SELECT ID FROM " + TABLE_INGREDIENCE + " WHERE NAME = " +"'"+ name +"'"
        var cursor: Cursor? = null
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return id
        }
        if (cursor.moveToFirst()) {
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))

            } while (cursor.moveToNext())
        }
        return id
    }

    fun selectallIngredience() : ArrayList<String>{
        val coun = ArrayList<String>()
        val selecQuery = "SELECT NAME FROM INGREDIENCE"
        val DB = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }

        var name: String

        if (cursor.moveToFirst()) {
            do {name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                coun.add(name)

            } while (cursor.moveToNext())
        }
        return coun
    }

    fun insertRECEPT(recept: SQLdata.Recept): Long{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TITLE,recept.title)
        contentValues.put(TYPE,recept.type)
        contentValues.put(CATEGORY,recept.category)
        contentValues.put(TIME,recept.time)
        contentValues.put(POSTUP,recept.postup)
        contentValues.put(QUANTITY,recept.quantity)
        contentValues.put(PORTION,recept.portion)
        contentValues.put(IMG,recept.img)

        val success = DB.insert(TABLE_RECEPT,null,contentValues)

        DB.close()
        return success
    }

    fun selectRECEPT(ID:Int):ArrayList<SQLdata.Recept>{
        var recept : ArrayList<SQLdata.Recept> = ArrayList<SQLdata.Recept>()
        val DB = this.readableDatabase
        val selecQuery = "SELECT * FROM " + TABLE_RECEPT + " WHERE ID = " + ID
        var cursor: Cursor? = null

        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        var title : String
        var postup: String
        var img : String
        var type : String
        var category : String
        var time : String
        var quantity : String
        var portion : Int
        var id:Int
        if (cursor.moveToFirst()){
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE))
                category = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY))
                time = cursor.getString(cursor.getColumnIndexOrThrow(TIME))
                postup = cursor.getString(cursor.getColumnIndexOrThrow(POSTUP))
                quantity = cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY))
                portion = cursor.getInt(cursor.getColumnIndexOrThrow(PORTION))
                img = cursor.getString(cursor.getColumnIndexOrThrow(IMG))
                val data = SQLdata.Recept(0,title,type,category,time,postup,quantity,portion, img)
                recept.add(data)
            }while (cursor.moveToNext())
        }
        DB.close()
        return recept

    }

    fun selectIDRECEPT(recept: SQLdata.Recept):ArrayList<SQLdata.Recept>{
        val DB = this.readableDatabase
        val selecQuery = "SELECT" + ID + "FROM" + TABLE_RECEPT + "WHERE"
        DB.close()
        return ArrayList()

    }

    fun deleteRecept(id : Int){
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        val success = DB.delete(TABLE_RECEPT, " $ID = $id ", null)
        DB.close()
    }

    fun insertSURIVONYrecept( surovinyRecept: SQLdata.SurovinyRecept ) : Long{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENCE_ID,surovinyRecept.ingredience_id)
        contentValues.put(RECEPT_ID,surovinyRecept.recept_id)
        contentValues.put(QUANTITY,surovinyRecept.quantity)

        val succes = DB.insert(TABLE_SUROVINY_RECEPT,null,contentValues)

        DB.close()
        return succes
    }

    fun selectOneSurRecept(int: Int): SQLdata.RvSurovinyRecept{
        val DB = this.readableDatabase
        val selecQuery = "SELECT "+ TABLE_SUROVINY_RECEPT+"."+ID+", " + TABLE_INGREDIENCE+"."+ NAME + ", " + TABLE_SUROVINY_RECEPT + "." + QUANTITY +
                " FROM " + TABLE_INGREDIENCE +
                " JOIN " + TABLE_SUROVINY_RECEPT+" ON "+TABLE_SUROVINY_RECEPT+"."+ INGREDIENCE_ID+" = "+ TABLE_INGREDIENCE+"."+ ID +
                " WHERE "+ TABLE_SUROVINY_RECEPT+"."+ ID +" = "+ int
        var name:String
        var quantity:String
        var id:Int = 0
        var cursor: Cursor? = null
        var suroviny:SQLdata.RvSurovinyRecept = SQLdata.RvSurovinyRecept(0,"","")
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return suroviny
        }
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                quantity = cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY))
                suroviny = SQLdata.RvSurovinyRecept(id, name, quantity)
            } while (cursor.moveToNext())
        }
        return suroviny
    }

    fun selectSUROVINYrecept(int: Int): ArrayList<SQLdata.RvSurovinyRecept>{
        var suroviny:ArrayList<SQLdata.RvSurovinyRecept> = ArrayList<SQLdata.RvSurovinyRecept>()
        val DB = this.readableDatabase
        val selecQuery = "SELECT "+ TABLE_SUROVINY_RECEPT+"."+ID+", " + TABLE_INGREDIENCE+"."+ NAME + ", " + TABLE_SUROVINY_RECEPT + "." + QUANTITY +
                            " FROM " + TABLE_INGREDIENCE +
                            " JOIN " + TABLE_SUROVINY_RECEPT+" ON "+TABLE_SUROVINY_RECEPT+"."+ INGREDIENCE_ID+" = "+ TABLE_INGREDIENCE+"."+ ID +
                 " WHERE "+ TABLE_SUROVINY_RECEPT+"."+ RECEPT_ID+" = "+ int
        var name:String
        var quantity:String
        var id:Int = 0
        var cursor: Cursor? = null
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()){
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                quantity = cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY))
                val data = SQLdata.RvSurovinyRecept(id,name,quantity)
                suroviny.add(data)
            }while (cursor.moveToNext())
        }
        DB.close()
        return suroviny
    }

    fun updateSur(surovinyRecept: SQLdata.SurovinyRecept ){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INGREDIENCE_ID,surovinyRecept.ingredience_id)
        contentValues.put(RECEPT_ID,surovinyRecept.recept_id)
        contentValues.put(QUANTITY,surovinyRecept.quantity)
        db.update(TABLE_SUROVINY_RECEPT,contentValues,"ID=${surovinyRecept.id}", arrayOf())
        db.close()
    }

    fun deleteSurRecept(id_ing: Int, id_recept : Int){
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        val success = DB.delete(TABLE_SUROVINY_RECEPT, " $INGREDIENCE_ID = $id_ing and $RECEPT_ID = $id_recept", null)
        DB.close()
        return
    }

    fun deleteSurRecept_id(id_recept: Int){
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        val success = DB.delete(TABLE_SUROVINY_RECEPT, "  $RECEPT_ID = $id_recept", null)
        DB.close()
        return
    }

    fun selectAllTitles() : ArrayList<String>{
        val coun = ArrayList<String>()
        val selecQuery = "SELECT "+ TITLE+"  FROM "+ TABLE_RECEPT
        val DB = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }

        var title: String

        if (cursor.moveToFirst()) {
            do {title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                coun.add(title)

            } while (cursor.moveToNext())
        }
        return coun
    }

    fun selectByTitle(title:String) : ArrayList<SQLdata.AraySearched>{
        var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
        val selecQuery = "SELECT $ID, $TITLE, $IMG FROM $TABLE_RECEPT WHERE $TITLE like '%$title%'"
        val DB = this.readableDatabase
        var cursor: Cursor? = null
        var title:String
        var img:String
        var id:Int = 0
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                img = cursor.getString(cursor.getColumnIndexOrThrow(IMG))
                val data = SQLdata.AraySearched(id,title,img)
                arraySearched.add(data)
            }while (cursor.moveToNext())
        }
        return arraySearched
    }

    fun selectTitleIMG(where : String) : ArrayList<SQLdata.AraySearched>{
        var arraySearched:ArrayList<SQLdata.AraySearched> = ArrayList<SQLdata.AraySearched>()
        val DB = this.readableDatabase
        val selecQuery = "SELECT DISTINCT $TABLE_RECEPT."+ ID+", $TABLE_RECEPT."+ TITLE+", $TABLE_RECEPT."+ IMG +
                " FROM "+ TABLE_RECEPT+", "+ TABLE_SUROVINY_RECEPT + " WHERE (SUROVINY_RECEPT.RECEPT_ID = RECEPT.ID) " + where
        var title:String
        var img:String
        var id:Int = 0
        var cursor: Cursor? = null
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()){
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                img = cursor.getString(cursor.getColumnIndexOrThrow(IMG))
                val data = SQLdata.AraySearched(id,title,img)
                arraySearched.add(data)
            }while (cursor.moveToNext())
        }
        return arraySearched
    }
    fun updateRecept(recept: SQLdata.Recept){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TITLE,recept.title)
        contentValues.put(TYPE,recept.type)
        contentValues.put(CATEGORY,recept.category)
        contentValues.put(TIME,recept.time)
        contentValues.put(POSTUP,recept.postup)
        contentValues.put(QUANTITY,recept.quantity)
        contentValues.put(PORTION,recept.portion)
        contentValues.put(IMG,recept.img)
        db.update(TABLE_RECEPT,contentValues,"ID=${recept.id}", arrayOf())
        db.close()
    }

    fun insertCalendar( Calendar: SQLdata.Calendar) : Long{
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(YEAR,Calendar.year)
        contentValues.put(MONTH,Calendar.month)
        contentValues.put(DAY,Calendar.day)
        contentValues.put(RECEPT_ID,Calendar.recept_id)
        contentValues.put(TYPE,Calendar.type)

        val succes = DB.insert(TABLE_CALENDAR,null,contentValues)
        DB.close()
        return succes
    }

    fun selectCalendarDay(date : SQLdata.date) : ArrayList<SQLdata.Calendar> {
        var arraySearched: ArrayList<SQLdata.Calendar> = ArrayList<SQLdata.Calendar>()
        var year = date.year
        var month = date.month
        var day = date.day
        val selecQuery = "SELECT * FROM $TABLE_CALENDAR WHERE $YEAR = $year and" +
                                                                    " $MONTH = $month and" +
                                                                    " $DAY = $day"
        val DB = this.readableDatabase
        var cursor: Cursor? = null
        var recept_id : Int
        var type: String
        var id: Int = 0
        try {
            cursor = DB.rawQuery(selecQuery, null)
        } catch (e: SQLiteException) {
            DB.execSQL(selecQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                year = cursor.getInt(cursor.getColumnIndexOrThrow(YEAR))
                month = cursor.getInt(cursor.getColumnIndexOrThrow(MONTH))
                day = cursor.getInt(cursor.getColumnIndexOrThrow(DAY))
                recept_id = cursor.getInt(cursor.getColumnIndexOrThrow(RECEPT_ID))
                type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE))
                val data = SQLdata.Calendar(id, year, month, day, recept_id, type)
                arraySearched.add(data)
            } while (cursor.moveToNext())
        }
        return arraySearched
    }

    companion object{
        private val DATABASE_NAME = "MyDB.db"
        private val TABLE_SPIZ = "SPIZ"
        private val TABLE_RECEPT = "RECEPT"
        private val TABLE_INGREDIENCE = "INGREDIENCE"
        private val TABLE_SUROVINY_RECEPT = "SUROVINY_RECEPT"
        private val TABLE_CALENDAR = "CALENDAR"
        private val ID = "ID"
        private val INGREDIENCE_ID = "INGREDIENCE_ID"
        private val TITLE = "TITLE"
        private val POSTUP = "POSTUP"
        private val IMG = "IMG"
        private val NAME = "NAME"
        private val QUANTITY = "QUANTITY"
        private val RECEPT_ID = "RECEPT_ID"
        private val TYPE = "TYPE"
        private val PORTION = "PORTION"
        private val TIME = "TIME"
        private val CATEGORY = "CATEGORY"
        private val YEAR = "YEAR"
        private val MONTH = "MONTH"
        private val DAY = "DAY"


    }

}
