package com.example.wtodo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wtodo.R
import com.example.wtodo.data.ToDoViewModel
import com.example.wtodo.data.models.ToDoData
import com.example.wtodo.databinding.FragmentListBinding
import com.example.wtodo.fragments.SharedViewModel
import com.example.wtodo.fragments.list.adapter.ListAdapter
import com.example.wtodo.fragments.list.adapter.SwipeToDelete
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class ListFragment : Fragment(){


    override fun onDestroyView() {//https://www.coder.work/article/131214
        binding = null
        super.onDestroyView()
    }


    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var binding: FragmentListBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment and binding that
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        binding = FragmentListBinding.bind(view)

        //set up recyclerView  初始化recyclerView
        binding?.let { setUpRecyclerView(it) } //binding非空则执行


        //get Databse Data
        initAppDatabaseData()


        //设置那个加号按键的事件
        binding?.floatingActionButton?.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }


        //使用MenuHost管理Menu
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider, SearchView.OnQueryTextListener {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                //绑定搜索功能按键
                val search=menu.findItem(R.id.menu_search)//尚不知databinding怎么用于Menu
                val searchView=search.actionView as? SearchView //androidx的
                searchView?.isSubmitButtonEnabled=true
                searchView?.setOnQueryTextListener(this)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_delete_all -> deleteAllDataNow()
                    R.id.menu_priority_high-> sortByPriorityHigh()
                    R.id.menu_priority_low-> sortByPriorityLow()
                }
                // Handle the menu selection
                return true
            }

            //实现搜索标签
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null){
                    searchThroughDatabase(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(query!=null){
                    searchThroughDatabase(query)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        return view
        /*为什么我这里使用了databinding还是返回view：“这里返回binding.root也是完全没有问题的，大型项目可能使用binding.root会更安全一些,我没有创建一个统一的databindingAdapter类，
        因为这个项目比较小所以我就没有那样子通过一个统一的类去管理各个按钮和视图的点击后操作，正常流程是需要创建一个Adapter类去管理的，
        而且需要修改XML来使用databinding，现在的安卓直接通过findbyid这个操作很可能没法找到目标然后动作就会绑定失败，基本都是通过databinding来操作各个组件了”*/
    }

    private fun setUpRecyclerView(binding: FragmentListBinding) {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        //增加控件动画
        recyclerView.itemAnimator =SlideInUpAnimator().apply {
            addDuration=100
        }
        swipeToDelete(recyclerView)//非空判断let{}
    }

    private fun initAppDatabaseData() {
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, {
            showEmptyDatabaseViews(it)
        })
    }


    //实现标签的按照priority排序展示在页面上
    private fun sortByPriorityLow() {
        mToDoViewModel.sortByLowPriority.observe(this,{adapter.setData(it)})
    }

    private fun sortByPriorityHigh() {
        mToDoViewModel.sortByHighPriority.observe(this,{adapter.setData(it)})
    }


    //向左滑动删除当前的标签
    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                //删除数据
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                //给用户一个删除后可以恢复数据的方法
                restoreDeletedData(viewHolder.itemView,itemToDelete)
            }
        }
        val itemTouchHelper =
            ItemTouchHelper(swipeToDeleteCallback)  //关于ItemTouchHelper：https://www.jianshu.com/p/d07fd08f72db
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    //恢复向左滑动删除的标签
    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snakeBar = Snackbar.make(
            view, "删除了'${deletedItem.title}'", Snackbar.LENGTH_LONG
        )
        snakeBar.setAction("取消"){
            mToDoViewModel.insertData(deletedItem)

        }
        snakeBar.show()
    }
    //判断数据库是否为空
    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding?.noDataImageView?.visibility = View.VISIBLE
            binding?.noDataTextView?.visibility = View.VISIBLE
        } else {
            binding?.noDataImageView?.visibility = View.INVISIBLE
            binding?.noDataTextView?.visibility = View.INVISIBLE
        }
    }


    //删除所有的标签
    private fun deleteAllDataNow() {
        val builder = AlertDialog.Builder(requireContext())//创建一个提示窗来提醒用户是否删除
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(), "成功移除所有标签", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("删除所有标签")
        builder.setMessage("确定要删除所有标签吗？")
        builder.create().show()
    }


    //实现搜索标签的数据库操作
    private fun searchThroughDatabase(query: String) {
        val searchQuery="%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observe(this,{ list->
            list?.let{
                adapter.setData(it)
            }
        })
    }


}