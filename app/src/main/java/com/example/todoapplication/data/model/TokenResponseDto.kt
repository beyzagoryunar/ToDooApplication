package com.example.todoapplication.data.model

data class TokenResponseDto(
    var accessToken  : String? = null,
    var refreshToken : String? = null,
    var userId       : Int?    = null

)
