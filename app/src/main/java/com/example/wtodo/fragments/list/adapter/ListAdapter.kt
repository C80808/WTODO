package com.example.wtodo.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wtodo.R
import com.example.wtodo.data.models.Priority
import com.example.wtodo.data.models.ToDoData
import com.example.wtodo.databinding.RowLayoutBinding
import com.example.wtodo.fragments.list.ListFragmentDirections

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    //Recycler


    var dataList= emptyList<ToDoData>()

    class MyViewHolder(binding:RowLayoutBinding):RecyclerView.ViewHolder(binding.root) {//
        val mTitle =binding.titleTxt
        val mDescriptionTxt=binding.descriptionTxt
        val mPriorityIndicator=binding.priorityIndicator
        val mRowBackground=binding.rowBackground
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)//加载自己创建的rowLayout来规定item样式
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//每个item绑定数据和相应点击事件
        holder.mTitle.text=dataList[position].title
        holder.mDescriptionTxt.text=dataList[position].description
        holder.mRowBackground.setOnClickListener {

            val action=ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)
        }
        when(dataList[position].priority){//小标签上面的priority提示颜色的改变
            Priority.HIGH-> holder.mPriorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
            Priority.MEDIUM-> holder.mPriorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.yellow))
            Priority.LOW-> holder.mPriorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.green))

        }
    }


    fun setData(toDoData: List<ToDoData>){//当前的数据每次重新加载Fragmentlist的时候都会更新一次此data
        val toDoDiffUtil=ToDoDiffUtil(dataList,toDoData)
        val toDoDiffResult=DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList=toDoData//保存了原有数据之后再更新
        toDoDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}