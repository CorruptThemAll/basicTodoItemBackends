package com.example.demo.service.impl

import com.example.demo.common.entity.User
import com.example.demo.common.http.JWT
import com.example.demo.common.repository.IUserRepository
import com.example.demo.security.JwtTokenProvider
import com.example.demo.service.IUserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl (
		private val IUserRepository: IUserRepository,
		private val jwtTokenProvider: JwtTokenProvider,
		private val authenticationManager: AuthenticationManager,
		private val passwordEncoder: PasswordEncoder
) : IUserService {
	/**
	 * Login a user and return a JWT token if successful
	 */
	override fun login(user: User): JWT {
		// Authenticate user through Spring Security
		val authentication = authenticationManager.authenticate(
			UsernamePasswordAuthenticationToken(user.username, user.password)
		)

		SecurityContextHolder.getContext().authentication = authentication

		// Generate JWT token
		val token = jwtTokenProvider.generateToken(user.username)
		return JWT(token)
	}

	/**
	 * Register a new user
	 */
	override fun register(username: String, password: String): User {
		if (IUserRepository.findByUsername(username) != null) {
			throw IllegalArgumentException("Username already exists")
		}
		// Encrypt password before saving
		return IUserRepository.save(User(
			username = username,
			password = passwordEncoder.encode(password)
		))
	}

	/**
	 * Validate a JWT token
	 */
	override fun validateToken(token: String): Boolean {
		return jwtTokenProvider.validateToken(token)
	}

	/**
	 * Get current authenticated user
	 */
	override fun getCurrentUser(): User? {
		val authentication = SecurityContextHolder.getContext().authentication
		val username = authentication.name
		return IUserRepository.findByUsername(username)
	}
}

