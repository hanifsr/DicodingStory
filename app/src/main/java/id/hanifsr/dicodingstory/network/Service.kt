package id.hanifsr.dicodingstory.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DicodingStoryService {

	@FormUrlEncoded
	@POST("register")
	suspend fun signUp(
		@Field("name") name: String,
		@Field("email") email: String,
		@Field("password") password: String
	): NetworkSignUp

	@FormUrlEncoded
	@POST("login")
	suspend fun signIn(
		@Field("email") email: String,
		@Field("password") password: String
	): NetworkSignIn

	@Multipart
	@POST("stories")
	suspend fun addNewStory(
		@Header("Authorization") authHeader: String,
		@Part("description") description: RequestBody,
		@Part file: MultipartBody.Part
	): NetworkAddNewStory

	@GET("stories")
	suspend fun getAllStories(
		@Header("Authorization") authHeader: String
	): NetworkStoryList
}

object DicodingStoryNetwork {

	private val retrofit = Retrofit.Builder()
		.baseUrl("https://story-api.dicoding.dev/v1/")
		.addConverterFactory(MoshiConverterFactory.create())
		.build()

	val dicodingStoryService: DicodingStoryService =
		retrofit.create(DicodingStoryService::class.java)
}