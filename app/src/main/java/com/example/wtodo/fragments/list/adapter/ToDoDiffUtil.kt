package com.example.wtodo.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.wtodo.data.models.ToDoData

class ToDoDiffUtil(//通过DiffUtil来托管用户界面更新操作来取代  notifyDataSetChanged()
    private val oldList:List<ToDoData>,
    private val newList: List<ToDoData>
):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==newList[newItemPosition]
                && oldList[oldItemPosition].title==newList[newItemPosition].title
                && oldList[oldItemPosition].description==newList[newItemPosition].description
                && oldList[oldItemPosition].priority==newList[newItemPosition].priority
    }

}