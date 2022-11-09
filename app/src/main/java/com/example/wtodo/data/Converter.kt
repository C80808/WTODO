package com.example.wtodo.data

import androidx.room.TypeConverter
import com.example.wtodo.data.models.Priority

class Converter {
    //使用Typeconverter来转换自定义变量Priority以便存入数据库的一个类
    @TypeConverter
    fun fromPriority(priority: Priority):String{
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}