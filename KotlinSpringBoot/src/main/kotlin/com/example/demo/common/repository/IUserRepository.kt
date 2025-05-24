package com.example.demo.common.repository

import com.example.demo.common.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IUserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
