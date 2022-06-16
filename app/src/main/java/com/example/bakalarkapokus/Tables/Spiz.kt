package com.example.bakalarkapokus.Tables


sealed class SQLdata {

    data class Spiz(
        val id: Int,
        val id_Ingredience: Int,
        val name: String
    )

    data class Recept(
        val id: Int,
        val title: String,
        val postup: String,
        val img: String
    )
    data class Suroviny(
        val id: Int,
        val name: String,
        val quantity: String= ""
    )
    data class Ingredience(
        val id: Int,
        val name: String
    )
    data class SurovinyRecept(
        val id: Int,
        val ingredience_id : Int,
        val recept_id:Int,
        val quantity: String
    )
}