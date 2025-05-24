package com.example.demo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            getJwtFromRequest(request)
                .takeIf { token -> StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) }
                ?.let { validToken ->
                    getAuthentication(validToken)
                        .onSuccess {
                            it?.let {
                                SecurityContextHolder.getContext().authentication = it
                            } ?: run {
                               logger.error("Username was not found in token")
                            }
                        }
                        .onFailure { ex ->
                            logger.error("Could not fetch user details or set authentication", ex)
                        }
                }
        }.onFailure { ex ->
            logger.error("Could not process authentication token", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(validToken: String): Result<Authentication?> {
        return runCatching {
            val username = jwtTokenProvider.getUsernameFromToken(validToken)
            if (username.isBlank()) {
                null
            } else {
                jwtTokenProvider.getAuthentication(validToken)
            }
        }
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return ""
    }
}
