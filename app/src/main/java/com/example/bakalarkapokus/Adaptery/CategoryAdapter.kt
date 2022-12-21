package com.example.bakalarkapokus.Adaptery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_category.view.*

class CategoryAdapter(private val sList: ArrayList<SQLdata.ArrayCategory>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int){
        }
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    class ViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view){
        var tvName = view.tvCategoryRv
        var ivIMG : ImageView = view.findViewById(R.id.ivCategory)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        return CategoryAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_category, parent, false),mListener
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
       val item = sList[position]
        holder.tvName.text = item.string
        holder.ivIMG.setImageResource(item.img)

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}