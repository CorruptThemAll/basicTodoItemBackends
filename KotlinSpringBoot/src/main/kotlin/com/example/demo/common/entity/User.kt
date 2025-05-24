package com.example.demo.common.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "users")
class User(
    @NotNull
    var username: String,
    @NotNull
    @Column(unique = true) var password: String
) : EntityClass()
