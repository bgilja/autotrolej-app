package com.example.autotrolejapp.home

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Line

var mExpandedPosition = -1
var previousExpandedPosition = -1

class LinesAdapter(private val listener: ViewHolder.Listener) : RecyclerView.Adapter<LinesAdapter.ViewHolder> () {
    var data =  emptyList<Line>()
        set(value) {
            field = value
            //TODO: hendlat na bolji nacin promjenu data u adapteru da ne krši cijelu i radi novu iz pocetka
            notifyDataSetChanged()
            mExpandedPosition = -1
            previousExpandedPosition = -1
        }

    override fun getItemCount() = data.size

    class ViewHolder (itemView: View, private val listener: Listener): RecyclerView.ViewHolder(itemView){
        val lineNumberBox: View = itemView.findViewById(R.id.view)
        val lineNumber: TextView = itemView.findViewById(R.id.line_number)
        val lineName: TextView = itemView.findViewById(R.id.line_name)
        //new
        val seeScheduleButton: Button = itemView.findViewById(R.id.schedule_button)
        val seeRouteButton: Button = itemView.findViewById(R.id.route_button)

        interface Listener {
            fun onScheduleClick(lineNumber: String)
            fun onRouteClick(lineNumber: String)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /*val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_line, parent, false)
        return ViewHolder(view)*/
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_line, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources
        //val layoutParams = holder.lineNumberBox.layoutParams as ConstraintLayout.LayoutParams
        holder.lineName.text = item.variantName
        holder.lineNumber.text = item.lineNumber
        holder.lineNumberBox.setBackgroundColor( when(item.area) {
            "Local" -> Color.parseColor("#28CCFA")
            "Wide" -> Color.parseColor("#F7572F")
            "Night"-> Color.parseColor("#1B4B66")
            else -> Color.parseColor("#ffffff")
        })

        holder.seeRouteButton.setBackgroundColor( when(item.area) {
            "Local" -> Color.parseColor("#2C7CA9")
            "Wide" -> Color.parseColor("#d13008")
            "Night"-> Color.parseColor("#1B4B66")
            else -> Color.parseColor("#ffffff")
        })

        holder.seeScheduleButton.setBackgroundColor( when(item.area) {
            "Local" -> Color.parseColor("#2C7CA9")
            "Wide" -> Color.parseColor("#d13008")
            "Night"-> Color.parseColor("#1B4B66")
            else -> Color.parseColor("#ffffff")
        })

        val isExpanded = position === mExpandedPosition
        holder.seeScheduleButton.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
        holder.seeRouteButton.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
        holder.itemView.isActivated = isExpanded
        val layoutParamsChanged = holder.lineNumberBox.layoutParams as ConstraintLayout.LayoutParams
        if (isExpanded) {
            //TODO: bug kod dodavanj ovog bottomMargina, nac mozda neko bolje rijesenje
            layoutParamsChanged.bottomMargin = 100
            holder.lineNumberBox.layoutParams = layoutParamsChanged;
            //to close other open lines
            previousExpandedPosition = position;
        }

        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }

        //TODO: ovo nije najbolji nacin kako implementirat listener >>> trebo bi bit implementiran u onCreateViewHolder
        holder.seeScheduleButton.setOnClickListener{
            listener.onScheduleClick(item.lineNumber)
        }

        holder.seeRouteButton.setOnClickListener{
            listener.onRouteClick(item.lineNumber)
        }

    }

}