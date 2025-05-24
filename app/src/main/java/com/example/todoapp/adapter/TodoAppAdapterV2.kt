package com.example.todoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ItemTodoBinding
import com.example.todoapp.model.TodoAppModel

class TodoAppAdapterV2(
    private val context:Context,
    private val asyncCallBack:DiffUtil.ItemCallback<TodoAppModel>,
    private val clickListenerFunc:(todo:TodoAppModel)-> Unit) :
    ListAdapter<TodoAppModel,TodoAppAdapterV2.ViewHolder>(asyncCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemTodoBinding = DataBindingUtil.inflate<ItemTodoBinding>(
            LayoutInflater.from(context),
            R.layout.item_todo,
            parent,
            false
        )
        return ViewHolder(itemTodoBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo=getItem(position)
        holder.bind(todo)
        holder.addClickListener(todo)
    }


    inner class ViewHolder(private val itemTodoBinding: ItemTodoBinding) :
        RecyclerView.ViewHolder(itemTodoBinding.root) {
        fun bind(todo: TodoAppModel) {
            itemTodoBinding.titleTodo.text = "Title : ${todo.title}"
            itemTodoBinding.descTodo.text = "Description: ${todo.description}"
        }
        fun addClickListener(todo: TodoAppModel?) {
            if (todo != null) {
               itemTodoBinding .root.setOnClickListener {
                    clickListenerFunc(todo)
                }
            }

        }
    }
}