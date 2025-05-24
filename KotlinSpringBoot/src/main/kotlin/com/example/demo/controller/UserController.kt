package com.example.demo.controller

import com.example.demo.common.entity.User
import com.example.demo.common.http.ApiResponse
import com.example.demo.extensions.toResponseEntity
import com.example.demo.service.IUserService
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class UserController(
    private val userService: IUserService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @JsonSerialize
    data class RegisterRequest(val username: String?, val password: String?)
    @JsonSerialize
    data class LoginRequest(val username: String?, val password: String?)
    @JsonSerialize
    data class LoginResponse(val token: String, val username: String)
    @JsonSerialize
    data class UserRegistrationResponse(val id: Long, val username: String)

    @PostMapping("/register")
    fun registerUser(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<ApiResponse<UserRegistrationResponse>> {
        val (username, password) = request

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return ApiResponse.error("Username and password cannot be empty").toResponseEntity()
        }

        return try {
            val user = userService.register(username, password)
            runCatching {
                UserRegistrationResponse(user.id, user.username)
            }.fold(
                { ApiResponse.Success(it) },
                { ApiResponse.error("Registration failed") }
            )
        } catch (ex: Exception) {
            logger.error("Error during registration", ex)
            ApiResponse.error("An unexpected error occurred");
        }.toResponseEntity()
    }

    @PostMapping("/login")
    fun loginUser(
        @RequestBody(required = true) request: LoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        // Validate request parameters
        val (username, password) = request

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return ApiResponse.error("Username and password cannot be empty").toResponseEntity()
        }
        return try {

            // Authenticate and generate JWT token
            val jwt = userService.login(User(username, password))

            // Return the token in the response
            ApiResponse.success(
                LoginResponse(
                    token = jwt.readPublicKey(),
                    username = username
                )
            )
        } catch (ex: Exception) {
            logger.error("Error during login", ex)
            ApiResponse.error("Login failed: ${ex.message}")
        }.toResponseEntity()
    }
}
