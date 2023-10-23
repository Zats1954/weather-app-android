package sk.solver.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.solver.weatherapp.models.WeatherResponse

class AdapterClass(private val dataList:  List<WeatherResponse>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolderClass(itemView)
        }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvCity.text = currentItem.name
        holder.rvCloud.text = currentItem.weather.get(0).description
        holder.rvTemperature.text = currentItem.main?.temp.toString() +" °C"
        holder.rvPressure.text = currentItem.main?.pressure.toString()
        holder.rvHumidity.text = currentItem.main?.humidity.toString() + "%"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

        class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {

            val rvCity:TextView = itemView.findViewById(R.id.city)
            val rvCloud:TextView = itemView.findViewById(R.id.cloud)
            val rvTemperature:TextView = itemView.findViewById(R.id.temp)
            val rvPressure:TextView = itemView.findViewById(R.id.pressure)
            val rvHumidity:TextView = itemView.findViewById(R.id.humidity)

        }

}