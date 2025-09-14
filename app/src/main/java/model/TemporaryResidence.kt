package model

data class TemporaryResidence(
    val id: Long,
    val residentId: Long,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val status: String
)
