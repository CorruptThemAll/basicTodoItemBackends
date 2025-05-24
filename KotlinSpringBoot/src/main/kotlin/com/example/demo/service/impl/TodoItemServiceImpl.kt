package com.example.demo.service.impl

import com.example.demo.common.http.ApiResponse
import com.example.demo.common.entity.TodoItem
import com.example.demo.common.repository.ITodoItemRepository
import com.example.demo.common.repository.IUserRepository
import com.example.demo.service.ITodoItemService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoItemServiceImpl(
    private val todoItemRepository: ITodoItemRepository,
    private val userRepository: IUserRepository
) : ITodoItemService {

    override fun getAll(): List<TodoItem> = todoItemRepository.findAll().toList()

    override fun getByOwnerId(userId: Long): ApiResponse<List<TodoItem>> {
        return userRepository.findById(userId)
            .map { user ->
                ApiResponse.success(todoItemRepository.findByUser(user))
            }
            .orElse(ApiResponse.error("User not found", 404))
    }

    override fun getTodoItemById(id: Long): ApiResponse<TodoItem> {
        return todoItemRepository.findById(id)
            .map { ApiResponse.success(it) }
            .orElse(ApiResponse.error("Todo item not found", 404))
    }

    @Transactional
    override fun createTodoItem(title: String, completed: Boolean, ownerId: Long): ApiResponse<TodoItem> {
        return userRepository.findById(ownerId)
            .map { user ->
                val todoItem = TodoItem(title = title, completed = completed, user = user)
                ApiResponse.success(todoItemRepository.save(todoItem))
            }
            .orElse(ApiResponse.error("User not found", 404))
    }

    @Transactional
    override fun updateTodoItem(id: Long, title: String, completed: Boolean, ownerId: Long): ApiResponse<TodoItem> {
        val todoItemResult = getTodoItemById(id)
        
        if (todoItemResult is ApiResponse.Error) {
            return todoItemResult
        }
        
        return userRepository.findById(ownerId)
            .map { user ->
                val todoItem = (todoItemResult as ApiResponse.Success).data
                todoItem.title = title
                todoItem.completed = completed
                todoItem.user = user
                ApiResponse.success(todoItemRepository.save(todoItem))
            }
            .orElse(ApiResponse.error("User not found", 404))
    }

    @Transactional
    override fun deleteTodoItem(id: Long): ApiResponse<Unit> {
        return todoItemRepository.findById(id)
            .map {
                todoItemRepository.delete(it)
                ApiResponse.success(Unit)
            }
            .orElse(ApiResponse.error("Todo item not found", 404))
    }
}
