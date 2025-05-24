package com.example.todoapp.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.db.TodoAppDatabase
import com.example.todoapp.model.TodoAppModel
import com.example.todoapp.repository.TodoAppRepository
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TodoAppViewModel(private val application: Application ) : AndroidViewModel(application) {
    private val mutableLiveTodoListData=MutableLiveData<Resource<List<TodoAppModel>>>()
    val liveTodoListData:LiveData<Resource<List<TodoAppModel>>> =mutableLiveTodoListData

    private val todoRepository by lazy{
        val todoDao=TodoAppDatabase.getTodoDBInstance(application).getTodoDao()
        TodoAppRepository(todoDao)
    }

    init{
        getAllTodos()
    }

    fun insertTodo(todo: TodoAppModel)= viewModelScope.launch (Dispatchers.IO){
            todoRepository.insertTodo(todo)
        }



    fun deleteTodo(todo: TodoAppModel)= viewModelScope.launch (Dispatchers.IO){
            todoRepository.deleteTodo(todo)
        }

    fun updateTodo(todo: TodoAppModel)= viewModelScope.launch (Dispatchers.IO){
            todoRepository.updateTodo(todo)
    }


    private  fun getAllTodos() = viewModelScope.launch(Dispatchers.Main) {
         todoRepository.getAllTodo()
             .flowOn(Dispatchers.IO)
             .collect{
                 mutableLiveTodoListData.postValue(it)
             }

        }



}