package com.example.autotrolejapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Line

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

class LinesAdapter : RecyclerView.Adapter<TextItemViewHolder> () {
    var data =  emptyList<Line>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.lineNumber + " " + item.variantName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.text_item_view, parent, false) as TextView
        return TextItemViewHolder(view)
    }
}