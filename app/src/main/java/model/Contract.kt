package model

data class Contract(
    val contractId: Long,
    val soHopDong: String,
    val ngayKy: String,
    val benA: String,
    val benBId: Int,
    val residentName: String? = null // 👉 để null khi không join residents
)
