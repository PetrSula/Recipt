package com.example.bakalarkapokus.Recept

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bakalarkapokus.AddRecept
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.items_searchable.view.*
import kotlinx.android.synthetic.main.recept_main.*
import java.io.File

class SearchAdapter (val context: Context, private val sList: ArrayList<SQLdata.AraySearched>): RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvTitle
        val ivIMG = view.ivRecept
        val tvEdit = view.ivEdit
        val tvDelete = view.ivDelete
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        return SearchAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_searchable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val item = sList[position]
        holder.tvName.text = item.title

        var requestOptions = RequestOptions()
            .fitCenter()
            .override(200,200)
        val file = File(item.img)
        var imgURI = Uri.fromFile(file)

        Glide.with(context)
            .load(imgURI)
            .apply (requestOptions)
            .into(holder.ivIMG)

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}