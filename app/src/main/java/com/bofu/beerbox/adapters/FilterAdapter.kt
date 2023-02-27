package com.bofu.beerbox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.databinding.RowFilterBinding
import com.bofu.beerbox.extensions.setOnSafeClickListener
import com.bofu.beerbox.models.Filter

class FilterAdapter(
    private val item: MutableList<Filter>,
    private val onClickListener: (Filter) -> Unit
): RecyclerView.Adapter<FilterAdapter.FilterHolder>(){


    fun update(newData: List<Filter>) {
        item.clear()
        item.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {

        val binding = RowFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterHolder(binding)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {

        holder.rowBeerFilter.isChecked = item[position].isChecked
        holder.rowBeerFilter.text = item[position].name
        holder.rowBeerFilter.textOn = item[position].name
        holder.rowBeerFilter.textOff = item[position].name

        holder.rowBeerFilter.setOnSafeClickListener {
            onClickListener(item[position])
        }
    }

    class FilterHolder(binding: RowFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        val rowBeerFilter: ToggleButton = binding.rowBeerFilter
    }
}