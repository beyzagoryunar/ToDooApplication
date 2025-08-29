    package com.example.todoapplication.api

    import android.R
    import com.example.todoapplication.data.model.LoginResponse
    import com.example.todoapplication.data.model.NotificationDTO
    import com.example.todoapplication.data.model.NotificationSendDTO
    import com.example.todoapplication.data.model.TaskModelDto
    import com.example.todoapplication.data.model.TokenResponseDto
    import com.example.todoapplication.data.model.User
    import com.example.todoapplication.screen.auth.LoginRequest
    import com.example.todoapplication.screen.auth.RegisterRequest
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.DELETE
    import retrofit2.http.GET
    import retrofit2.http.Header
    import retrofit2.http.POST
    import retrofit2.http.PUT
    import retrofit2.http.Path

    interface ApiService {
        @POST("api/Auth/register")
        suspend fun registerUser(@Body user: User): Response<User>

        @POST("api/Auth/Login")
        suspend fun loginUser(@Body user: User): Response<TokenResponseDto>

        @GET("gettodobyid/{user_id}")
        suspend fun getTasks(
            @Header("Authorization") token: String,
            @Path("user_id") userId: Int
        ): Response<List<TaskModelDto>>

        @POST("addtodo")
    suspend fun addTask(
            @Header("Authorization") token: String,
            @Body task: TaskModelDto
        ): Response<TaskModelDto>

        @DELETE("deletetodo/{id}")
        suspend fun deleteTask(
            @Header("Authorization") token: String,
            @Path("id") id: Int): Response<Unit>

        @PUT("updatetodo/{id}")
        suspend fun updateTask(
            @Header("Authorization") token: String,
            @Path("id") id: Int,
            @Body task: TaskModelDto
        ): Response<TaskModelDto>

        @POST("login")
        suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

        @POST("register")
        suspend fun registerUser(@Body request: RegisterRequest): Response<Unit>

        @POST("notification/{id}")
        suspend fun InsertOneSignalPlayerId(
            @Header("Authorization") token: String,
            @Path("id") id: Int,
            @Body request: NotificationDTO)
        : Response<Unit>

        @DELETE("notification/delete/{userId}/{playerId}")
        suspend fun deleteOneSignalPlayerId(
            @Header("Authorization") token: String,
            @Path("userId") userId: Int,
            @Path("playerId") playerId: String
        ): Response<Unit>

        @POST("notification/send/{userId}")
        suspend fun SendNotification(
            @Header("Authorization") token: String,
            @Path("userId") userId: Int,
            ): Response<Unit>
    }
