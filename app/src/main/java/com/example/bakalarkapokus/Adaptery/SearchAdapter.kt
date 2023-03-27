package com.example.bakalarkapokus.Adaptery

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakalarkapokus.Aktivity.AddCalendarActivity
import com.example.bakalarkapokus.Aktivity.AdvanceActivity
import com.example.bakalarkapokus.Aktivity.CalendarActivity
import com.example.bakalarkapokus.Aktivity.SearchedActivity
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import com.example.bakalarkapokus.fragments.DayFragment
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.items_searchable.view.*
import java.io.File

class SearchAdapter (val context: Context, private val sList: ArrayList<SQLdata.AraySearched>): RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int){
        }
    }

    fun setOnItemClickListener(listener : SearchAdapter.onItemClickListener){
        mListener = listener
    }

    class ViewHolder(view: View, listener: SearchAdapter.onItemClickListener) : RecyclerView.ViewHolder(view){
        val tvName = view.tvTitle
        val ivIMG = view.ivRecept


        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        return SearchAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_searchable, parent, false), mListener
        )
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val item = sList[position]
        holder.tvName.text = item.title

//        loadDataFromAsset(holder,item.img)
//        var requestOptions = RequestOptions()
//            .fitCenter()
//            .override(200,200)
//        val file = File(item.img)
//        var imgURI = Uri.fromFile(file)
//
//        Glide.with(context)
//            .load(imgURI)
//            .apply (requestOptions)
//            .into(holder.ivIMG)
//        if (context !is CalendarActivity){
//            holder.ivEdit.visibility = View.GONE
//            holder.ivDelete.visibility = View.GONE
//        }



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
    }



    override fun getItemCount(): Int {
        return sList.size
    }

    interface MyFragmentCallback {
        fun onFragmentClick(fragment: DayFragment)
    }
}