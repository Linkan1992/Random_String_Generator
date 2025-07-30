package com.linkan.randomstringgenerator.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.linkan.randomstringgenerator.databinding.ItemRandomStringRowBinding
import com.linkan.randomstringgenerator.domain.model.RandomText

class RandomStringAdapter : RecyclerView.Adapter<RandomStringAdapter.RandomStringViewHolder>() {

    private var onItemClickListener : ((RandomText, List<RandomText>) -> Unit)? = null

    fun deleteOnItemClickListener(listener : (RandomText, List<RandomText>) -> Unit){
        onItemClickListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<RandomText>(){
        override fun areItemsTheSame(oldItem: RandomText, newItem: RandomText): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RandomText, newItem: RandomText): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerDiffList = AsyncListDiffer(this, diffCallback)

    var stringList : List<RandomText>
        get() = recyclerDiffList.currentList
        set(value) = recyclerDiffList.submitList(value)

    class RandomStringViewHolder(val mBinding: ItemRandomStringRowBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomStringAdapter.RandomStringViewHolder {
        return RandomStringViewHolder(ItemRandomStringRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RandomStringAdapter.RandomStringViewHolder, position: Int) {
        val randomText : RandomText = stringList[position]
        holder.mBinding.mtvRandomString.text = randomText.value
        holder.mBinding.mtvStringLength.text = "Length - ${randomText.length}"
        holder.mBinding.mtvTimestamp.text = randomText.created
        holder.mBinding.imvDelete.apply {
            setOnClickListener {
                onItemClickListener?.let { listener ->
                    listener(randomText, stringList)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return stringList.size
    }
}