package com.example.demo.common.repository

import com.example.demo.common.entity.TodoItem
import com.example.demo.common.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ITodoItemRepository : JpaRepository<TodoItem, Long> {
    fun findByUser(user: User): List<TodoItem>
}
