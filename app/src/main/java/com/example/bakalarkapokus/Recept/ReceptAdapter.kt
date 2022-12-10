package com.example.bakalarkapokus.Recept

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.green
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.Ingredience
import com.example.bakalarkapokus.Tables.SQLdata
import com.example.bakalarkapokus.data
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.recept_suroviny.view.*

class ReceptAdapter(val context: Context, private val sList: ArrayList<SQLdata.RvSurovinyRecept>) : RecyclerView.Adapter<ReceptAdapter.ViewHolder>(){
    val spis= DBHelper(context).selectSpiz()



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvNamers
        val tvQuantity = view.tvQuantytirs
        val ivCkeckon = view.ivCheck

//        val tvEdit = view.ivEdit
//        val tvDelete = view.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceptAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recept_suroviny,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = sList.get(position)
        holder.tvName.text = item.name
        holder.tvQuantity.text = item.quantity
        val addOK = spis.any { it.name == item.name }
        if (addOK){
            DrawableCompat.setTint(DrawableCompat.wrap(holder.ivCkeckon.drawable),ContextCompat.getColor(context,R.color.primary))
        }
//        else{
//            DrawableCompat.setTint(DrawableCompat.wrap(holder.ivCkeckon.drawable),ContextCompat.getColor(context,R.color.))
//        }
//        var color = Color.parseColor("#000000")
//        holder.ivCkeckon.drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}