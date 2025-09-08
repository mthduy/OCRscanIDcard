package model

data class User(
    val id: Int = 0,
    val fullName: String,
    val phone: String,
    val birthDate: String,
    val username: String,
    val password: String,
    val role: String, // mặc định user
    val gmail: String,
    val residentId: Int? = null  // <-- thêm trường khóa ngoại
)
