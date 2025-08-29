//package com.example.todoapplication.api
//
//import com.google.gson.annotations.SerializedName
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.Header
//import retrofit2.http.POST
//
//data class NotificationRequest(
//    @SerializedName("app_id") val appId: String,
//    @SerializedName("include_player_ids") val playerIds: List<String>,
//    @SerializedName("headings") val headings: Map<String, String>,
//    @SerializedName("contents") val contents: Map<String, String>,
//    @SerializedName("send_after") val sendAfter: String
//)
//
//interface OneSignalApiService {
//    @POST("notifications")
//    suspend fun scheduleNotification(
//        @Header("Authorization") apiKey: String,
//        @Body request: NotificationRequest
//    ): Response<Unit>
//}