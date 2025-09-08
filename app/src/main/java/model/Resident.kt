package model

import android.graphics.Bitmap

data class Resident(
    var residentId: Int = 0,
    var fullName: String,
    var birthDate: String,
    var sex: String,
    var idNumber: String,
    var origin: String,
    var residence: String,
    var expiry: String,
    var signature: ByteArray? = null // Nếu dùng ảnh chữ ký, null nếu chưa có
)
