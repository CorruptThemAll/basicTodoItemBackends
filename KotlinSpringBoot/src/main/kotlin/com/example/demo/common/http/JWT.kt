package com.example.demo.common.http

/**
 * JWT token
 */
data class JWT(private val token: String) {
	fun readPublicKey(): String = token
}