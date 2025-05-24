package com.example.todoapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.adapter.TodoAppAdapterV2
import com.example.todoapp.databinding.ActivityTodoAppBinding
import com.example.todoapp.databinding.DialogTodoBinding
import com.example.todoapp.model.TodoAppModel
import com.example.todoapp.utils.Resource
import com.example.todoapp.viewmodel.TodoAppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TodoAppActivity: AppCompatActivity() {
    private lateinit var todoAppActivityBinding:ActivityTodoAppBinding
    private lateinit var todoAppViewModel: TodoAppViewModel
    private val todoAppAdapter by lazy {
        TodoAppAdapterV2(this,ITEM_CALLBACK){ todo->
            displayAlertBox(todo)
        }
    }
    private val adapterList by lazy {
        todoAppAdapter.currentList
    }

    private lateinit var todoAppRv:RecyclerView
    private val ITEM_CALLBACK by lazy {
        object : DiffUtil.ItemCallback<TodoAppModel>() {
            override fun areItemsTheSame(oldItem: TodoAppModel, newItem: TodoAppModel): Boolean {
                return oldItem.id==newItem.id
            }

            override fun areContentsTheSame(oldItem: TodoAppModel, newItem: TodoAppModel): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       todoAppActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_todo_app)
        intiUi()
        setUpObservers()

    }

    private fun setUpObservers() {
        todoAppViewModel.liveTodoListData.observe(this){
            when(it){
                is Resource.Loading-> {}

                is Resource.Error-> Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()

                is Resource.Success-> todoAppAdapter.submitList(it.data)

            }
        }
    }

    private fun intiUi(){
        todoAppViewModel= ViewModelProvider(this)[TodoAppViewModel::class.java]
        todoAppRv=todoAppActivityBinding.todoRv
        val linearLayoutManager=LinearLayoutManager(this)
        todoAppRv.layoutManager=linearLayoutManager
        todoAppRv.addItemDecoration(DividerItemDecoration(this,linearLayoutManager.orientation))
        todoAppRv.adapter=todoAppAdapter
        todoAppActivityBinding.btnAdd.apply { setOnClickListener{ openAddOrUpdateDialog() } }
        filterQuery()
    }
    private fun filterQuery(){
        lifecycleScope.launch() {
            val queryBox= todoAppActivityBinding.searchInputBox.editText
            getWatcherForQuery(queryBox!!)
                .debounce(500)
                .map { it.toString().trim() }
                .filter {adapterList.isNotEmpty()}
                .map {query->
                    adapterList
                       .filter {todo->
                           todo.title.contains(query, ignoreCase = true)||
                                   todo.description.contains(query, ignoreCase = true)
                       }
                }.collect{
                    todoAppAdapter.submitList(it)
                }
        }
    }
    private fun getWatcherForQuery(editText: EditText):Flow<CharSequence>{
         return callbackFlow{
            val watcher= object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                   trySend(s.toString())
                }

            }
             editText.addTextChangedListener(watcher)
             awaitClose{editText.removeTextChangedListener(watcher)}
        }
    }

    private fun openAddOrUpdateDialog(todo:TodoAppModel?=null) {
        val dialogTodoBinding=DataBindingUtil.inflate<DialogTodoBinding>(layoutInflater,R.layout.dialog_todo,null,false)
        val alertDialog=MaterialAlertDialogBuilder(this)
            .setView(dialogTodoBinding.root)
            .setCancelable(true)
            .create()
        todo?.let {
            dialogTodoBinding.edtTodo.editText?.setText(todo.title)
            dialogTodoBinding.edtDesc.editText?.setText(todo.description)
            dialogTodoBinding.btnSubmit.text = getString(R.string.update)
        }


        setUpFieldObservers(dialogTodoBinding)
        dialogTodoBinding.btnCancel.setOnClickListener{alertDialog.dismiss()}
        dialogTodoBinding.btnSubmit.setOnClickListener {
            if(dialogTodoBinding.btnSubmit.isEnabled){
                val titleTodo=dialogTodoBinding.edtTodo.editText?.text.toString()
                val descTodo=dialogTodoBinding.edtDesc.editText?.text.toString()
                if(todo!=null)
                    todoAppViewModel.updateTodo(TodoAppModel(id = todo.id, title = titleTodo, description = descTodo))
                else
                    todoAppViewModel.insertTodo(TodoAppModel(title = titleTodo, description = descTodo))

                alertDialog.dismiss()
            }
        }
        alertDialog.show()

    }

    private fun setUpFieldObservers(dialogTodoBinding:DialogTodoBinding){
       val titleFlow= dialogTodoBinding.edtTodo.editText!!
            .textChanges()
            .map { it.toString().trim() }

        val descFlow=dialogTodoBinding.edtDesc.editText!!
            .textChanges()
            .map { it.toString().trim() }

        lifecycleScope.launch {
            combine(titleFlow,descFlow){
                title, desc ->  title to desc
            }
                .collect{
                   val valid= it.first.isNotBlank() && it.second.isNotBlank()
                    if(!valid) {
                        dialogTodoBinding.btnSubmit.isEnabled = false
                        if (it.first.isBlank() && it.second.isNotBlank()) {
                            dialogTodoBinding.edtTodo.error = "Required"
                            dialogTodoBinding.edtDesc.error=null
                            dialogTodoBinding.edtDesc.boxStrokeColor = getColor(R.color.btn_floating)
                        }
                        else if (it.second.isBlank() && it.first.isNotBlank()) {
                            dialogTodoBinding.edtDesc.error = "Required"
                            dialogTodoBinding.edtTodo.error=null
                            dialogTodoBinding.edtTodo.boxStrokeColor = getColor(R.color.btn_floating)
                        }
                        else{
                            dialogTodoBinding.edtTodo.error = "Required"
                            dialogTodoBinding.edtDesc.error = "Required"

                        }
                    }else{
                        dialogTodoBinding.btnSubmit.isEnabled = true
                        dialogTodoBinding.edtTodo.error = null
                        dialogTodoBinding.edtDesc.error = null
                        dialogTodoBinding.edtTodo. boxStrokeColor = getColor(R.color.btn_floating)
                        dialogTodoBinding.edtDesc.boxStrokeColor = getColor(R.color.btn_floating)
                    }
                }
        }


    }
    private fun displayAlertBox(todo: TodoAppModel) {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage("Alert")
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete"){ _, _ ->
                todoAppViewModel.deleteTodo(todo)
            }
            .setNeutralButton("Update"){ dialog, _ ->
                dialog.dismiss()
                openAddOrUpdateDialog(todo)
            }.create().show()

    }

    private fun EditText.textChanges(): Flow<CharSequence> {
        return callbackFlow {
            val watcher= object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                 s?.let { trySend(s) }
                }

            }

            addTextChangedListener(watcher)
            text?.let { trySend(it) }
            awaitClose { removeTextChangedListener(watcher) }
        }
    }
}