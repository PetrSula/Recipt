package com.example.bakalarkapokus.Adaptery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.*
import com.example.bakalarkapokus.Aktivity.AddRecept
import com.example.bakalarkapokus.Aktivity.AdvanceActivity
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*

class SurovinyAdapter (val context: Context, private val sList: ArrayList<SQLdata.Suroviny>): RecyclerView.Adapter<SurovinyAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvName
        val tvQuantyti = view.tvQuantyti
        val tvEdit = view.ivEdit
        val tvDelete = view.ivDelete
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurovinyAdapter.ViewHolder {
        return SurovinyAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sList[position]
        holder.tvName.text = item.name
        holder.tvQuantyti.text = item.quantity
        if (context is AdvanceActivity){
            holder.tvEdit.visibility = View.GONE
        }
        holder.tvEdit.setOnClickListener{
            if ( context is AddRecept){
                context.editSurovinaRV(item)
            }
        }
        DrawableCompat.setTint(
            DrawableCompat.wrap(holder.tvEdit.drawable),
            ContextCompat.getColor(context,R.color.third))
        holder.tvDelete.setOnClickListener {
            if (context is AddRecept ) {
                context.deleteSurovinu(item)
            }else if (context is AdvanceActivity){
                context.deleteSurovinu(item)
            }
        }
        DrawableCompat.setTint(DrawableCompat.wrap(holder.tvDelete.drawable),ContextCompat.getColor(context,R.color.ikon))

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}