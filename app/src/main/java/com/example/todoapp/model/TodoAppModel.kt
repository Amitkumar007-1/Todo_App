package com.example.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Todo")
data class TodoAppModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,

    val  title:String,
    val description:String
)
