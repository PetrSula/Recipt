package com.example.bakalarkapokus.Tables

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList


class DBHelper ( context: Context) :SQLiteOpenHelper(context, DATABASE_NAME,null,5) {

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
        contentValues.put(POSTUP,recept.postup)
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
        var id:Int
        if (cursor.moveToFirst()){
            do {id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                postup = cursor.getString(cursor.getColumnIndexOrThrow(POSTUP))
                img = cursor.getString(cursor.getColumnIndexOrThrow(IMG))
                val data = SQLdata.Recept(0,title,postup,img)
                recept.add(data)
            }while (cursor.moveToNext())
        }
        return recept
    }

    fun selectIDRECEPT(recept: SQLdata.Recept):ArrayList<SQLdata.Recept>{
        val DB = this.readableDatabase
        val selecQuery = "SELECT" + ID + "FROM" + TABLE_RECEPT + "WHERE"
        return ArrayList()

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

    /*fun selectSUROVINYrecept(int: Int): ArrayList<SQLdata.SurovinyRecept>{

    }*/

    companion object{
        private val DATABASE_NAME = "MyDB"
        private val TABLE_SPIZ = "SPIZ"
        private val TABLE_RECEPT = "RECEPT"
        private val TABLE_INGREDIENCE = "INGREDIENCE"
        private val TABLE_SUROVINY_RECEPT = "SUROVINY_RECEPT"
        private val ID = "ID"
        private val INGREDIENCE_ID = "INGREDIENCE_ID"
        private val TITLE = "TITLE"
        private val POSTUP = "POSTUP"
        private val IMG = "IMG"
        private val NAME = "NAME"
        private val QUANTITY = "QUANTITY"
        private val RECEPT_ID = "RECEPT_ID"

    }

}
