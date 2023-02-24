package com.bofu.beerbox.adapters

import android.animation.AnimatorInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.R
import com.bofu.beerbox.databinding.RowBeerBinding
import com.bofu.beerbox.extensions.loadImage
import com.bofu.beerbox.models.Beer

class BeerAdapter(
    private val item: MutableList<Beer>,
    private val onClickListener: (Beer) -> Unit
): RecyclerView.Adapter<BeerAdapter.BeerHolder>(){

    private var previousPosition = -1

    fun update(newData: List<Beer>) {
        item.clear()
        item.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerHolder {
        val binding = RowBeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeerHolder(binding)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: BeerHolder, position: Int) {
        holder.beerName.text = item[position].id.toString() + " " + item[position].name
        holder.beerTagline.text = item[position].tagline
        holder.beerDescription.text = item[position].description
        holder.beerImg.loadImage(item[position].image_url)

        holder.beerMask.setOnClickListener {
            onClickListener(item[position])
        }

        // call Animation function
        setAnimation(holder.itemView, position)
    }

    class BeerHolder(binding: RowBeerBinding) : RecyclerView.ViewHolder(binding.root) {
        val beerName: TextView = binding.rowBeerName
        val beerTagline: TextView = binding.rowBeerTagline
        val beerDescription: TextView = binding.rowBeerDescription
        val beerImg: ImageView = binding.rowBeerImg
        val beerMask: ImageView = binding.rowBeerMask
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {

        // scrolling to bottom
        if (position > previousPosition) {

            AnimatorInflater.loadAnimator(viewToAnimate.context, R.animator.fede_in_up).apply {
                setTarget(viewToAnimate)
                start()
            }
        }

        // scrolling to top
        /*
        else {

            AnimatorInflater.loadAnimator(viewToAnimate.context, R.animator.fede_in_down).apply {
                setTarget(viewToAnimate)
                start()
            }
        }
        */
        previousPosition = position
    }
}