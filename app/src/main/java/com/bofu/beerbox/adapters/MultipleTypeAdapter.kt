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
import com.bofu.beerbox.databinding.RowBlankBinding
import com.bofu.beerbox.extensions.loadImage
import com.bofu.beerbox.extensions.setOnSafeClickListener
import com.bofu.beerbox.models.Beer

class MultipleTypeAdapter(
    private val item: MutableList<Beer>,
    private val onClickListener: (Beer) -> Unit,
    private var numExtraType: Int = 2
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var previousPosition = -1

    fun update(newData: List<Beer>) {
        item.clear()
        item.addAll(newData)
        notifyDataSetChanged()
    }

    fun enableRefresh(bool: Boolean){
        numExtraType = when(bool){
            true -> 2
            false -> 0
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == TYPE_BEER) {
            val binding = RowBeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BeerHolder(binding)
        } else {
            val binding = RowBlankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BlankHolder(binding)
        }
    }

    override fun getItemCount() = item.size + numExtraType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            TYPE_BEER -> {

                val adjustedPosition = when(numExtraType){
                    0 -> {
                        position
                    }
                    else -> {
                        position - 1
                    }
                }

                val beerHolder = holder as BeerHolder
                beerHolder.beerName.text = item[adjustedPosition].id.toString() + " " + item[adjustedPosition].name
                beerHolder.beerTagline.text = item[adjustedPosition].tagline
                beerHolder.beerDescription.text = item[adjustedPosition].description
                beerHolder.beerImg.loadImage(item[adjustedPosition].image_url)

                beerHolder.beerMask.setOnSafeClickListener {
                    onClickListener(item[adjustedPosition])
                }

                // call Animation function
                setAnimation(beerHolder.itemView, adjustedPosition)
            }
            TYPE_FIRST -> {
                val blankHolder = holder as BlankHolder
                if(item.size > numExtraType) {
                    blankHolder.arrowPrevious.visibility = View.VISIBLE
                    blankHolder.arrowNext.visibility = View.GONE
                } else {
                    blankHolder.arrowPrevious.visibility = View.GONE
                    blankHolder.arrowNext.visibility = View.GONE
                }
            }
            TYPE_LAST -> {
                val blankHolder = holder as BlankHolder
                if(item.size > numExtraType) {
                    blankHolder.arrowPrevious.visibility = View.GONE
                    blankHolder.arrowNext.visibility = View.VISIBLE
                } else {
                    blankHolder.arrowPrevious.visibility = View.GONE
                    blankHolder.arrowNext.visibility = View.GONE
                }
            }
        }

    }

    class BeerHolder(binding: RowBeerBinding) : RecyclerView.ViewHolder(binding.root) {
        val beerName: TextView = binding.rowBeerName
        val beerTagline: TextView = binding.rowBeerTagline
        val beerDescription: TextView = binding.rowBeerDescription
        val beerImg: ImageView = binding.rowBeerImg
        val beerMask: ImageView = binding.rowBeerMask
    }

    class BlankHolder(binding: RowBlankBinding) : RecyclerView.ViewHolder(binding.root) {
        val arrowPrevious: ImageView = binding.rowBlankArrowPrevious
        val arrowNext: ImageView = binding.rowBlankArrowNext
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

    override fun getItemViewType(position: Int): Int {
        when(numExtraType){
            0 -> {
                return TYPE_BEER
            }
            else -> {
                return when (position) {
                    0 -> {
                        TYPE_FIRST
                    }
                    (item.size + numExtraType - 1) -> {
                        TYPE_LAST
                    }
                    else -> {
                        TYPE_BEER
                    }
                }
            }
        }



    }

    companion object {
        private const val TYPE_FIRST = 0
        private const val TYPE_BEER = 1
        private const val TYPE_LAST = 2
    }
}