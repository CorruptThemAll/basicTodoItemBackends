package com.example.demo.security

import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val userDetailsService: UserDetailsService
) {
    private var jwtSecret: String

    private var jwtExpirationMs: Long = 0

    private val key: SecretKey by lazy {
        if (jwtSecret.isNotEmpty()) {
            Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        } else {
            // Generate a secure key if no secret is provided
            Keys.secretKeyFor(SignatureAlgorithm.HS512)
        }
    }

    private val log: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    init {
        val env = Dotenv.load()
        jwtSecret = env.get("JWT_SECRET", "default_JWT_secret_if_env_variable_is_not_found")
        jwtExpirationMs = env.get("JWT_EXPIRATION", "3600000").toLong()
    }

	fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            log.error("Token validation error", e)
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val username = getUsernameFromToken(token)
        val userDetails = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }
}
