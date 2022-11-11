package com.example.wtodo.fragments.list.adapter

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

 abstract class OnRecyclerItemClickListener(private var mRecyclerView: RecyclerView) :RecyclerView.OnItemTouchListener {
    //监听标签的触摸事件的自定义监听器,主要用来实现长按点击的事件
    //GestureDetectorCompat安卓sdk手势判断器
    private var mGestureDetectorCompat:GestureDetectorCompat

    init {
        mGestureDetectorCompat=GestureDetectorCompat(mRecyclerView.context,ItemTouchHelperGestureListener())
    }


    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mGestureDetectorCompat.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        mGestureDetectorCompat.onTouchEvent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    abstract fun onLongClick(viewHolder: RecyclerView.ViewHolder?)//后续在fragment中重写这个方法来完成自定义当前页面的当前Item长按事件

    inner class ItemTouchHelperGestureListener: GestureDetector.SimpleOnGestureListener() {

        //长按事件
        override fun onLongPress(e: MotionEvent?) {
            e?.let {
                val childViewUnder= mRecyclerView.findChildViewUnder(e.x,e.y)

                val childViewHolder= childViewUnder?.let { it1 ->
                    mRecyclerView.getChildViewHolder(
                        it1
                    )
                }
                onLongClick(childViewHolder)
            }

        }

    }

}