package com.example.todoapp.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.model.TodoAppModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface TodoDao {
    @Query("Select * from todo order by id ASC")
     fun getAllTodoList(): Flow<List<TodoAppModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo:TodoAppModel)

    @Delete
    suspend fun deleteTodo(todo:TodoAppModel)

    @Update
    suspend fun updateTodo(todo:TodoAppModel)


}