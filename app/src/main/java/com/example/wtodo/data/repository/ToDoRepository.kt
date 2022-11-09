package com.example.wtodo.data.repository

import androidx.lifecycle.LiveData
import com.example.wtodo.data.ToDoDao
import com.example.wtodo.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {
    //和DAO层进行交互的一个类

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()
    val sortByHighPriority:LiveData<List<ToDoData>> =toDoDao.sortByHighPriority()
    val sortByLowPriority:LiveData<List<ToDoData>> =toDoDao.sortByLowPriority()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData){
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAllData(){
        toDoDao.deleteAllData()
    }

    fun searchDatabase(searchQuery: String):LiveData<List<ToDoData>>{
        return toDoDao.searchDatabase(searchQuery)
    }

}