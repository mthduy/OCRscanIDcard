package database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import model.Resident
import model.User

class ResidentDAO(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /** Thêm cư dân mới, trả về residentId */
    fun addResident(resident: Resident): Long {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_HO_TEN, resident.fullName)
                put(DatabaseHelper.COL_NGAY_SINH, resident.birthDate)
                put(DatabaseHelper.COL_GIOI_TINH, resident.sex)
                put(DatabaseHelper.COL_CCCD, resident.idNumber)
                put(DatabaseHelper.COL_QUE_QUAN, resident.origin)
                put(DatabaseHelper.COL_NOI_THUONG_TRU, resident.residence)
                put(DatabaseHelper.COL_NGAY_HET_HAN, resident.expiry)
                put(DatabaseHelper.COL_SIGNATURE, resident.signature)
            }
            db.insert(DatabaseHelper.TABLE_RESIDENTS, null, values)
        } finally {
            db.close()
        }
    }

    /** Tạo user dựa trên residentId đã lưu, tránh tạo lại bản cư dân */
    fun addResidentWithUser(residentId: Int, resident: Resident): Boolean {
        // --- Sinh username/password ---
        val username = generateUsername(resident.fullName, resident.idNumber)
        val password = resident.idNumber.takeLast(4) // 4 số cuối CCCD

        val user = User(
            fullName = resident.fullName,
            phone = "",
            birthDate = resident.birthDate,
            username = username,
            password = password,
            role = "user",
            gmail = "",
            residentId = residentId
        )

        val userDAO = UserDAO(context)

        // Kiểm tra username đã tồn tại chưa
        if (userDAO.isUsernameExists(username)) return false

        return userDAO.registerUser(user)
    }
    fun getResidentById(id: Int): Resident? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RESIDENTS,
            null,
            "${DatabaseHelper.COL_RESIDENT_ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )

        var resident: Resident? = null
        if (cursor.moveToFirst()) {
            resident = Resident(
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HO_TEN)),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_SINH)),
                sex = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GIOI_TINH)),
                idNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CCCD)),
                origin = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUE_QUAN)),
                residence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOI_THUONG_TRU)),
                expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_HET_HAN)),
                signature = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SIGNATURE))
            )

            Log.d("SQLite", "Lấy thông tin cư dân thành công!")
        } else {
            Log.d("SQLite", "Không tìm thấy cư dân với id: $id")
        }

        cursor.close()
        db.close()
        return resident
    }


    /** Thêm resident và tự động tạo user, trả về true nếu thành công */
    fun addResidentWithUser(resident: Resident): Boolean {
        val residentId = addResident(resident)
        if (residentId == -1L) return false

        // --- Sinh username/password ---
        val username = generateUsername(resident.fullName, resident.idNumber)
        val password = resident.idNumber.takeLast(4) // 4 số cuối CCCD

        val user = User(
            fullName = resident.fullName,
            phone = "",
            birthDate = resident.birthDate,
            username = username,
            password = password,
            role = "user",
            gmail = "",
            residentId = residentId.toInt()
        )

        val userDAO = UserDAO(context)

        // Kiểm tra username đã tồn tại chưa
        if (userDAO.isUsernameExists(username)) return false

        return userDAO.registerUser(user)
    }

    /** Tạo username: không dấu, chữ thường, fullName + 3 số cuối CCCD */
    private fun generateUsername(fullName: String, idNumber: String): String {
        val noAccent = removeAccent(fullName)
        val last3 = idNumber.takeLast(3)
        return (noAccent.replace("\\s+".toRegex(), "") + last3).lowercase()
    }
    fun getResidentByUser(user: User): Resident? {
        val resId = user.residentId ?: return null   // Nếu null → trả về null
        return getResidentById(resId)               // resId là Int, OK
    }



    /** Xóa dấu tiếng Việt */
    private fun removeAccent(str: String): String {
        var temp = str
        temp = temp.replace("Đ", "D").replace("đ", "d")
        val unicodeMap = mapOf(
            "[áàảãạăắằẳẵặâấầẩẫậ]" to "a",
            "[ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬ]" to "A",
            "[éèẻẽẹêếềểễệ]" to "e",
            "[ÉÈẺẼẸÊẾỀỂỄỆ]" to "E",
            "[íìỉĩị]" to "i",
            "[ÍÌỈĨỊ]" to "I",
            "[óòỏõọôốồổỗộơớờởỡợ]" to "o",
            "[ÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]" to "O",
            "[úùủũụưứừửữự]" to "u",
            "[ÚÙỦŨỤƯỨỪỬỮỰ]" to "U",
            "[ýỳỷỹỵ]" to "y",
            "[ÝỲỶỸỴ]" to "Y"
        )
        for ((regex, replace) in unicodeMap) {
            temp = temp.replace(regex.toRegex(), replace)
        }
        return temp
    }
    fun updateResident(resident: Resident): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_HO_TEN, resident.fullName)
            put(DatabaseHelper.COL_NGAY_SINH, resident.birthDate)
            put(DatabaseHelper.COL_GIOI_TINH, resident.sex)
            put(DatabaseHelper.COL_CCCD, resident.idNumber)
            put(DatabaseHelper.COL_QUE_QUAN, resident.origin)
            put(DatabaseHelper.COL_NOI_THUONG_TRU, resident.residence)
            put(DatabaseHelper.COL_NGAY_HET_HAN, resident.expiry)
            // signature nếu có chỉnh sửa thì thêm ở đây
        }
        val result = db.update(
            DatabaseHelper.TABLE_RESIDENTS,
            values,
            "${DatabaseHelper.COL_RESIDENT_ID}=?",
            arrayOf(resident.residentId.toString())
        )
        db.close()
        return result
    }


}
