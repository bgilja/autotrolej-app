package com.example.autotrolejapp.station

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.ScheduleStation


class StationsAdapter(): RecyclerView.Adapter<StationsAdapter.ViewHolder> () {

    var data =  emptyList<ScheduleStation>()
        set(value) {
            field = value
            //TODO: hendlat na bolji nacin promjenu data u adapteru da ne krši cijelu i radi novu iz pocetka
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val lineName: TextView = itemView.findViewById(R.id.line_variant_name)
        val timeOfArrival: TextView = itemView.findViewById(R.id.time_of_arrival)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_schedule_station, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.lineName.text = item.lineVariantId
        holder.timeOfArrival.text = item.timeStr
    }

}