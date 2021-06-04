package com.example.autotrolejapp.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Line

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

class LinesAdapter : RecyclerView.Adapter<LinesAdapter.ViewHolder> () {
    var data =  emptyList<Line>()
        set(value) {
            field = value
            //TODO: hendlat na bolji nacin promjenu data u adapteru da ne krši cijelu i radi novu iz pocetka
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val lineNumberBox: View = itemView.findViewById(R.id.view)
        val lineNumber: TextView = itemView.findViewById(R.id.line_number)
        val lineName: TextView = itemView.findViewById(R.id.line_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_line, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources

        holder.lineName.text = item.variantName
        holder.lineNumber.text = item.lineNumber
        holder.lineNumberBox.setBackgroundColor( when(item.area) {
            "Local" -> Color.parseColor("#28CCFA")
            "Wide" -> Color.parseColor("#F7572F")
            "Night"-> Color.parseColor("#1B4B66")
            else -> Color.parseColor("#ffffff")
        })

    }
}