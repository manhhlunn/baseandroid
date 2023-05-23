package com.example.baseandroid.data.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class RefreshTokenResponse(
    var access_token: String,
    var refresh_token: String
)