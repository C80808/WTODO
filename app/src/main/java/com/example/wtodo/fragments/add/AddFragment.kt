package com.example.wtodo.fragments.add

import android.os.Bundle

import android.view.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.findNavController
import com.example.wtodo.R

import com.example.wtodo.data.ToDoViewModel

import com.example.wtodo.data.models.ToDoData
import com.example.wtodo.databinding.FragmentAddBinding
import com.example.wtodo.fragments.SharedViewModel


class AddFragment : Fragment() {


    private val mToDoViewModel:ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()
    private  var  title: EditText?=null
    private  var priority: Spinner?=null
    private  var description:EditText?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_add, container, false)

        val binding=FragmentAddBinding.bind(view)

        binding.prioritiesSpinner.onItemSelectedListener=mSharedViewModel.listener

        title=binding.titleEt
        priority=binding.prioritiesSpinner
        description=binding.descriptionEt
        @Suppress("DEPRECATION")//使用MenuHost的话会出现返回键无法被nav处理导致返回键无法有作用，所以这里就直接使用的老的方法来处理Menu上添加按钮事件
        setHasOptionsMenu(true)


        return binding.root
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.add_fragment_menu, menu)",
        "com.example.wtodo.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_add){
            insertDataToDb()
        }
        @Suppress("DEPRECATION")
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {

        val mTitle=title?.text.toString()
        val mPriority=priority?.selectedItem.toString()
        val mDescription=description?.text.toString()
        if(mSharedViewModel.verifyDataFromUser(mTitle,mDescription)){
            //数据进入数据库
            val newData=ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"成功添加!",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"标题和描述都需要填写",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        title=null
        priority=null
        description=null
        super.onDestroyView()
    }
}