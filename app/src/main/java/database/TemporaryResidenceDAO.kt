package database

import android.content.ContentValues
import android.content.Context
import model.TemporaryResidence

class TemporaryResidenceDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addRequest(residentId: Long, startDate: String, endDate: String, reason: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_RESIDENT_ID, residentId)
            put(DatabaseHelper.COL_START_DATE, startDate)
            put(DatabaseHelper.COL_END_DATE, endDate)
            put(DatabaseHelper.COL_REASON, reason)
            put(DatabaseHelper.COL_STATUS, "pending")
        }
        return db.insert(DatabaseHelper.TABLE_TEMP_RESIDENCE, null, values)
    }

    fun getAllRequests(): List<TemporaryResidence> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_TEMP_RESIDENCE, null, null, null, null, null, null)
        val list = mutableListOf<TemporaryResidence>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TemporaryResidence(
                        id = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COL_TR_ID)),
                        residentId = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COL_RESIDENT_ID)),
                        startDate = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_START_DATE)),
                        endDate = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_END_DATE)),
                        reason = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_REASON)),
                        status = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS))
                    )
                )
            }
        }
        return list
    }

    fun updateStatus(id: Long, status: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_STATUS, status)
        }
        return db.update(
            DatabaseHelper.TABLE_TEMP_RESIDENCE,
            values,
            "${DatabaseHelper.COL_TR_ID}=?",
            arrayOf(id.toString())
        )
    }

    fun getRequestsByResident(residentId: Long): List<TemporaryResidence> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TEMP_RESIDENCE,
            null,
            "${DatabaseHelper.COL_RESIDENT_ID}=?",
            arrayOf(residentId.toString()),
            null, null, null
        )
        val list = mutableListOf<TemporaryResidence>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TemporaryResidence(
                        id = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COL_TR_ID)),
                        residentId = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COL_RESIDENT_ID)),
                        startDate = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_START_DATE)),
                        endDate = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_END_DATE)),
                        reason = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_REASON)),
                        status = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS))
                    )
                )
            }
        }
        return list
    }
}
