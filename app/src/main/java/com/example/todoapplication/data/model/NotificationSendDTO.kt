package com.example.todoapplication.data.model

data class NotificationSendDTO(
    val Title: String,
    val Message: String,
    val PlayerIds: List<String>
)
