package database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import model.User

class AdminDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getAdminById(id: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COL_ID}=? AND ${DatabaseHelper.COL_ROLE}=?",
            arrayOf(id.toString(), "admin")
        )

        var admin: User? = null
        if (cursor.moveToFirst()) {
            admin = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULLNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE)),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDATE)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROLE)),
                gmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GMAIL))
            )
        }
        cursor.close()
        db.close()
        return admin
    }

}
