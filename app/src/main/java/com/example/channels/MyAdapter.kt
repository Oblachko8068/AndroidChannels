package com.example.channels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private var dataList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val originalDataList: List<String> = dataList // Используем originalDataList для сохранения исходного списка данных

    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            // Если строка поиска пустая или равна null, покажем весь список данных
            dataList = originalDataList
        } else {
            // Если введен текст поиска, отфильтруем список по этому тексту
            dataList = originalDataList.filter { item ->
                item.contains(query, ignoreCase = true)
            }
        }

        notifyDataSetChanged()
    }



    // Создайте класс ViewHolder, который будет содержать элементы вашего макета
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textViewItem)
    }

    // Создайте ViewHolder и свяжите его с макетом элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    // Наполните ViewHolder данными из списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.textView.text = data
    }

    // Верните количество элементов в списке
    override fun getItemCount(): Int {
        return dataList.size
    }

}
