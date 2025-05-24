package com.example.todoapp.repository

import com.example.todoapp.Dao.TodoDao
import com.example.todoapp.model.TodoAppModel
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform

class TodoAppRepository(private val todoDao:TodoDao) {

     suspend fun insertTodo(todo:TodoAppModel){
         todoDao.insertTodo(todo)
    }

    suspend fun deleteTodo(todo:TodoAppModel){
         todoDao.deleteTodo(todo)
     }


    suspend fun updateTodo(todo:TodoAppModel){
        todoDao.updateTodo(todo)
    }

     fun getAllTodo(): Flow<Resource<List<TodoAppModel>>> {
         return   todoDao.getAllTodoList()
             .transform {
                 emit(Resource.Loading)
                 emit(Resource.Success(it))
             }.catch { emit(Resource.Error(it.message)) }

     }
}