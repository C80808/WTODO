package com.example.wtodo.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.wtodo.data.models.ToDoData
import com.example.wtodo.data.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application):AndroidViewModel(application) {

    //通过Repository使用courutine的管理数据库的一个类,也有不使用协程的方法
    //MVVM框架下的View Model层

     val toDoDao=ToDoDatabase.getDatabase(application).toDoDao()
     val repository:ToDoRepository
     val getAllData: LiveData<List<ToDoData>>
     val sortByHighPriority:LiveData<List<ToDoData>>
     val sortByLowPriority:LiveData<List<ToDoData>>



    init {
        repository= ToDoRepository(toDoDao)
        getAllData=repository.getAllData
        sortByHighPriority=repository.sortByHighPriority
        sortByLowPriority=repository.sortByLowPriority
    }

    fun insertData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertData(toDoData)
        }
    }

    fun updateData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateData(toDoData)
        }
    }

    fun deleteData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteData(toDoData)
        }
    }

    fun deleteAllData(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllData()
        }
    }

    fun searchDatabase(searchQuery:String):LiveData<List<ToDoData>>{
        return repository.searchDatabase(searchQuery)
    }
}