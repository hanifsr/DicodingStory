package id.hanifsr.dicodingstory.domain

data class Story(
	val id: String,
	val name: String,
	val description: String,
	val photoUrl: String,
	val createdAt: String,
	val lat: Int,
	val lon: Int
)

data class User(
	val userId: String,
	val name: String,
	val token: String,
	val isLogin: Boolean
)