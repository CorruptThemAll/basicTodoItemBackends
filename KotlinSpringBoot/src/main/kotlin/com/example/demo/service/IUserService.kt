package com.example.demo.service

import com.example.demo.common.entity.User
import com.example.demo.common.http.JWT

interface IUserService {
	/*
	* Login a user and return a JWT token if successful
	*/
	fun login(user: User): JWT;

	/**
	 * Register a new user
	 */
	fun register(username: String, password: String): User;

	/**
	 * Validate a JWT token
	 */
	fun validateToken(token: String): Boolean;

	/**
	 * Get current authenticated user
	 */
	fun getCurrentUser(): User?;
}