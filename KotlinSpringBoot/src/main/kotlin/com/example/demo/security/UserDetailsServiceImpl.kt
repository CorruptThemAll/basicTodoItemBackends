package com.example.demo.security

import com.example.demo.common.repository.IUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val IUserRepository: IUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = IUserRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        
        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities("USER")
            .build()
    }
}
