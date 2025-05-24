package com.example.demo.common.entity

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class TodoItem(
    var title: String,
    var completed: Boolean = false,
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User
) : EntityClass()