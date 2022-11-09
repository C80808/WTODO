package com.example.wtodo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.wtodo.R
import com.example.wtodo.data.ToDoViewModel
import com.example.wtodo.data.models.ToDoData
import com.example.wtodo.databinding.FragmentUpdateBinding
import com.example.wtodo.fragments.SharedViewModel


class UpdateFragment : Fragment() {


    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private  var mbinding: FragmentUpdateBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_update, container, false)
        val binding = FragmentUpdateBinding.bind(view)//绑定当前视图
        binding.currentTitleEt.setText(args.currentItem.title)
        binding.currentDescriptionEt.setText(args.currentItem.description)//使用navgation,parcelable数据数据绑定得到的数据
        binding.currentPrioritiesSpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener
        mbinding = binding

        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
        return view
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> deleteCurrentItem()
        }
        @Suppress("DEPRECATION")
        return super.onOptionsItemSelected(item)
    }

    private fun deleteCurrentItem() {
        val builder = AlertDialog.Builder(requireContext())//创建一个提示窗来提醒用户是否删除
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(), "成功移除‘${args.currentItem.title}’", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("删除'${args.currentItem.title}'")
        builder.setMessage("确定要删除'${args.currentItem.title}'吗？")
        builder.create().show()
    }


    private fun updateItem() {
        val title = mbinding?.currentTitleEt?.text.toString()
        val description = mbinding?.currentDescriptionEt?.text.toString()
        val priority = mbinding?.currentPrioritiesSpinner?.selectedItem.toString()
        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val updateItem = ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(priority),
                description
            )
            mToDoViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "更新成功", Toast.LENGTH_SHORT)
                .show()//用户的界面提示
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)//通过navController调用返回上层的的操作
        } else {
            Toast.makeText(requireContext(), "标题和描述都不能为空", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        mbinding=null
        super.onDestroyView()

    }
}