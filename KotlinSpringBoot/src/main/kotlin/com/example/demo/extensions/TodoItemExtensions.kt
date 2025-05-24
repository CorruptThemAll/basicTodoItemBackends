package com.example.demo.extensions

import com.example.demo.common.entity.TodoItem
import com.example.demo.common.http.ApiResponse
import com.example.demo.controller.TodoController.TodoItemResponse
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

/**
 * Converts a TodoItem entity to a TodoItemResponse DTO
 */
fun TodoItem.toResponse(): TodoItemResponse {
    return TodoItemResponse(
        id = this.id,
        title = this.title,
        completed = this.completed,
        ownerId = this.user.id
    )
}

fun <T> ApiResponse<T>.toResponseEntity(): ResponseEntity<ApiResponse<T>> {
    return when (this) {
        is ApiResponse.Success -> ResponseEntity.ok(this)
        is ApiResponse.Error -> ResponseEntity.status(this.code).body(this)
		is ApiResponse.SuccessNoData -> ResponseEntity.ok(this)
	}
}
