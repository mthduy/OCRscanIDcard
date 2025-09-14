package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import model.User

class UserDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.readableDatabase  // Mở một lần

    fun keepDatabaseOpen() {
        // Không làm gì cả, chỉ để giữ DB mở
    }

    // Thêm user mới (đăng ký)
    fun registerUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_FULLNAME, user.fullName)
            put(DatabaseHelper.COL_PHONE, user.phone)
            put(DatabaseHelper.COL_BIRTHDATE, user.birthDate)
            put(DatabaseHelper.COL_USERNAME, user.username)
            put(DatabaseHelper.COL_PASSWORD, user.password)
            put(DatabaseHelper.COL_ROLE, user.role)
            put(DatabaseHelper.COL_GMAIL, user.gmail)
            put(DatabaseHelper.COL_RESIDENT_ID, user.residentId)

        }

        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // Kiểm tra username tồn tại
    fun isUsernameExists(username: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COL_USERNAME}=?",
            arrayOf(username)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
    // Lấy tên user từ residentId
    fun getUserNameByResidentId(residentId: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COL_FULLNAME),
            "${DatabaseHelper.COL_RESIDENT_ID} = ?",
            arrayOf(residentId.toString()),
            null, null, null
        )

        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULLNAME))
        }
        cursor.close()
        db.close()
        return name
    }

    // Đăng nhập user
    fun loginUser(username: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COL_USERNAME}=? AND ${DatabaseHelper.COL_PASSWORD}=?",
            arrayOf(username, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULLNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE)),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDATE)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROLE)),
                gmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GMAIL)),
                residentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RESIDENT_ID))

                )
        }
        cursor.close()
        db.close()
        return user
    }



}
