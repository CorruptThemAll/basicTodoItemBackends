package com.example.demo.controller

import com.example.demo.common.http.ApiResponse
import com.example.demo.extensions.toResponse
import com.example.demo.extensions.toResponseEntity
import com.example.demo.service.ITodoItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todo")
class TodoController(private val todoItemService: ITodoItemService) {
	data class TodoItemResponse(
		val id: Long,
		val title: String,
		val completed: Boolean,
		val ownerId: Long
	)

	data class TodoItemRequest(
		val title: String,
		val completed: Boolean = false,
		val ownerId: Long
	)

	@GetMapping("/getAll")
	fun getAllItems(): ResponseEntity<ApiResponse<List<TodoItemResponse>>> {
		return todoItemService.getAll()
			.map { it.toResponse() }
			.let { ResponseEntity.ok(ApiResponse.success(it))}
	}

	@GetMapping
	fun get(
		@RequestParam ownerId: Long?
	): ResponseEntity<ApiResponse<List<TodoItemResponse>>> {
		return ownerId?.let { userId ->
			todoItemService.getByOwnerId(userId)
			.map { items -> items.map { it.toResponse() } }
			.toResponseEntity()
		} ?: ApiResponse.error("Missing ownerId", 400).toResponseEntity()
	}
	
	@PostMapping
	fun add(@RequestBody todoItemRequest: TodoItemRequest): ResponseEntity<ApiResponse<TodoItemResponse>> {
		return todoItemService.createTodoItem(
			todoItemRequest.title,
			todoItemRequest.completed,
			todoItemRequest.ownerId
		).map { it.toResponse() }
			.toResponseEntity()
	}
	
	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: Long?): ResponseEntity<ApiResponse<Unit>> {
		return todoItemService.deleteTodoItem(id?: 0).toResponseEntity()
	}
	
	@PutMapping("/{id}")
	fun update(
		@PathVariable id: Long,
		@RequestBody todoItemRequest: TodoItemRequest
	): ResponseEntity<ApiResponse<TodoItemResponse>> {
		return todoItemService.updateTodoItem(
			id,
			todoItemRequest.title,
			todoItemRequest.completed,
			todoItemRequest.ownerId
		).map { it.toResponse() }
			.toResponseEntity()
	}
}