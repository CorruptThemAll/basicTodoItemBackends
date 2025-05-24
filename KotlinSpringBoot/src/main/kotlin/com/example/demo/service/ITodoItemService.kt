package com.example.demo.service

import com.example.demo.common.http.ApiResponse
import com.example.demo.common.entity.TodoItem

interface ITodoItemService {
    fun getAll(): List<TodoItem>
    fun getByOwnerId(userId: Long): ApiResponse<List<TodoItem>>
    fun getTodoItemById(id: Long): ApiResponse<TodoItem>
    fun createTodoItem(title: String, completed: Boolean, ownerId: Long): ApiResponse<TodoItem>
    fun updateTodoItem(id: Long, title: String, completed: Boolean, ownerId: Long): ApiResponse<TodoItem>
    fun deleteTodoItem(id: Long): ApiResponse<Unit>
}
