package com.example.bakalarkapokus.Tables

import java.io.Serializable


sealed class SQLdata {

    data class Spiz(
        val id: Int,
        val id_Ingredience: Int,
        val name: String
    )

    data class Recept(
        val id: Int,
        val title: String,
        val type: String,
        val category: String,
        val time: String,
        val postup: String,
        val quantity: String = "",
        val portion: Int,
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
    data class RvSurovinyRecept(
        val id: Int,
        val name: String,
        val quantity: String
    )
    data class AraySearched(
        val id: Int,
        val title: String,
        val img: String,
    ) : Serializable

    data class ArrayCategory(
        val id: Int,
        val string: String,
        val img: Int,

    )
    data class ArrayCSVRecept(
        val id: Int,
        val title: String,
        val type: String,
        val category: String,
        val suroviny: Suroviny,
        val time: String,
        val postup: String,
        val portion: Int
    )
}