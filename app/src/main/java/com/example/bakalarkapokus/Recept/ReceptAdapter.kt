package com.example.bakalarkapokus.Recept

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.DBHelper
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.items_recept_suroviny.view.*

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
            LayoutInflater.from(parent.context).inflate(R.layout.items_recept_suroviny,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = sList.get(position)
        holder.tvName.text = item.name
        holder.tvQuantity.text = item.quantity
        val addOK = spis.any { it.name == item.name }
        if (addOK){

            DrawableCompat.setTint(DrawableCompat.wrap(holder.ivCkeckon.drawable),ContextCompat.getColor(context,R.color.ikon))
        }
        else{val drawable = ContextCompat.getDrawable(context,R.drawable.ic_not_in)
            holder.ivCkeckon.setImageDrawable(drawable)
            DrawableCompat.setTint(DrawableCompat.wrap(holder.ivCkeckon.drawable),ContextCompat.getColor(context,R.color.third))
        }
//        var color = Color.parseColor("#000000")
//        holder.ivCkeckon.drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}

