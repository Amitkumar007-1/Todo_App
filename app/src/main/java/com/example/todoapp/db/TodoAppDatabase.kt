package com.example.todoapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.Dao.TodoDao
import com.example.todoapp.model.TodoAppModel

@Database(entities = [TodoAppModel::class], version = 1, exportSchema = false)
abstract class TodoAppDatabase: RoomDatabase() {
    abstract fun getTodoDao():TodoDao


    companion object{
        @Volatile private var INSTANCE:TodoAppDatabase?=null

        fun getTodoDBInstance(context: Context):TodoAppDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,TodoAppDatabase::class.java,"todo_schema"
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }
}