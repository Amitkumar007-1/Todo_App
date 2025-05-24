package com.example.todoapp.repository

import com.example.todoapp.Dao.TodoDao
import com.example.todoapp.model.TodoAppModel
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class TodoAppRepository(private val todoDao:TodoDao) {

     suspend fun insertTodo(todo:TodoAppModel){
         println("Inserting")
         todoDao.insertTodo(todo)
//        return flow{
//            emit(Resource.Loading)
//            todoDao.insertTodo(todo)
//            emit(Resource.Success(todo))
//        }.catch { emit(Resource.Error(it.message)) }
    }

    suspend fun deleteTodo(todo:TodoAppModel){
//        return flow{
//            emit(Resource.Loading)
//            emit(Resource.Success(todo))
//        }.catch { emit(Resource.Error(it.message)) }
        println("Deleting")
         todoDao.deleteTodo(todo)

     }


    suspend fun updateTodo(todo:TodoAppModel){
//        return flow{
//            emit(Resource.Loading)
//            emit(Resource.Success(todo))
//        }.catch { emit(Resource.Error(it.message)) }
        println("Updating")
        todoDao.updateTodo(todo)
    }

     fun getAllTodo(): Flow<Resource<List<TodoAppModel>>> {
         println("Fetching List")
         return   todoDao.getAllTodoList()
             .transform {
                 emit(Resource.Loading)
                 emit(Resource.Success(it))
             }.catch { emit(Resource.Error(it.message)) }

     }
}