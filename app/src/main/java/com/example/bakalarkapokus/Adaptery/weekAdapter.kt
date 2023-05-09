package com.example.bakalarkapokus.Adaptery

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakalarkapokus.Aktivity.CalendarActivity
import com.example.bakalarkapokus.Aktivity.SpizActivity
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import com.example.bakalarkapokus.fragments.DayFragment
import com.example.bakalarkapokus.fragments.WeekFragment
import kotlinx.android.synthetic.main.items_meal.view.*
import kotlinx.android.synthetic.main.items_row.view.*
import java.io.File

class weekAdapter ( val context: Context, val items: ArrayList<SQLdata.Week>,val fragment: String):
    RecyclerView.Adapter<weekAdapter.ViewHolder>(){
    lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int){
        }
        fun imageDelClick(position: Int){
        }
        fun imageEditClick(position: Int){
        }
    }

    fun setOnItemClickListener(listener : weekAdapter.onItemClickListener){
        mListener = listener
    }
//    fun setOnImageClickListener(listener: weekAdapter.onItemClickListener.){
//        mListener = listener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): weekAdapter.ViewHolder {
        return weekAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_meal,
                parent,
                false
            ),mListener
        )
    }

    override fun onBindViewHolder(holder: weekAdapter.ViewHolder, position: Int) {
        val item = items.get(position)
        holder.tvTitle.text= item.title
        holder.tvtype.text = item.type


        val path = item.img
        val check : Boolean = "pictures/" in path
        if (check) {
            try {
                var ims = context.getResources().getAssets().open(path)
                var drawable = Drawable.createFromStream(ims, null)
                holder.ivIMG.setImageDrawable(drawable)
            } catch (e: Exception) {
                return
            }
        }else{
            val file = File(path)
            var imgURI = Uri.fromFile(file)
            Glide.with(context)
                .load(imgURI)
                .into(holder.ivIMG)
        }
        if (fragment == "DayFragment"){
            holder.tvtype.visibility = View.GONE
        }
        holder.ivDel.setOnClickListener {
            mListener.imageDelClick(item.id)
        }
        holder.ivEdit.setOnClickListener {
            mListener.imageEditClick(item.id)
        }
        DrawableCompat.setTint(
            DrawableCompat.wrap(holder.ivEdit.drawable),
            ContextCompat.getColor(context,R.color.secondary))
        DrawableCompat.setTint(DrawableCompat.wrap(holder.ivDel.drawable),ContextCompat.getColor(context,R.color.black))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View, listener:weekAdapter.onItemClickListener) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.tvTitle
        val tvtype = view.tvtype
        val ivIMG = view.ivRecept
        val ivEdit = view.iv_meal_Edit
        val ivDel = view.iv_meal_Del

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        init {
            ivDel.setOnClickListener {
                listener.imageDelClick(adapterPosition)
            }
        }init {
            ivEdit.setOnClickListener {
                listener.imageEditClick(adapterPosition)
            }
        }
    }
}