package database

import android.content.ContentValues
import android.content.Context
import model.Contract


class ContractDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addContract(contract: Contract): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_SO_HOP_DONG, contract.soHopDong)
            put(DatabaseHelper.COL_NGAY_KY, contract.ngayKy)
            put(DatabaseHelper.COL_BEN_A, contract.benA)
            put(DatabaseHelper.COL_BEN_B_ID, contract.benBId)
        }
        val id = db.insert(DatabaseHelper.TABLE_CONTRACTS, null, values)
        db.close()
        return id
    }

    fun getContractsByResident(residentId: Int): List<Contract> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Contract>()

        val query = """
        SELECT c.${DatabaseHelper.COL_CONTRACT_ID}, 
               c.${DatabaseHelper.COL_SO_HOP_DONG}, 
               c.${DatabaseHelper.COL_NGAY_KY}, 
               c.${DatabaseHelper.COL_BEN_A}, 
               c.${DatabaseHelper.COL_BEN_B_ID}, 
               r.${DatabaseHelper.COL_HO_TEN} AS resident_name
        FROM ${DatabaseHelper.TABLE_CONTRACTS} c
        JOIN ${DatabaseHelper.TABLE_RESIDENTS} r 
          ON c.${DatabaseHelper.COL_BEN_B_ID} = r.${DatabaseHelper.COL_RESIDENT_ID}
        WHERE c.${DatabaseHelper.COL_BEN_B_ID} = ?
    """

        val cursor = db.rawQuery(query, arrayOf(residentId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val contract = Contract(
                    contractId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTRACT_ID)),
                    soHopDong = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SO_HOP_DONG)),
                    ngayKy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_KY)),
                    benA = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BEN_A)),
                    benBId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BEN_B_ID)),
                    residentName = cursor.getString(cursor.getColumnIndexOrThrow("resident_name")) // ✅ tên cư dân
                )
                list.add(contract)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

}
