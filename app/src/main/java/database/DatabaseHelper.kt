package database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "app_db"
        const val DATABASE_VERSION = 9

        // Bảng users (đã có)
        const val TABLE_USERS = "users"
        const val COL_ID = "id"
        const val COL_FULLNAME = "full_name"
        const val COL_PHONE = "phone"
        const val COL_BIRTHDATE = "birth_date"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_ROLE = "role"
        const val COL_GMAIL = "gmail"

        // Bảng residents
        const val TABLE_RESIDENTS = "residents"
        const val COL_RESIDENT_ID = "resident_id"
        const val COL_HO_TEN = "ho_ten"
        const val COL_NGAY_SINH = "ngay_sinh"
        const val COL_GIOI_TINH = "gioi_tinh"
        const val COL_CCCD = "cccd"
        const val COL_QUE_QUAN = "que_quan"

        const val COL_NOI_THUONG_TRU = "noi_thuong_tru"

        const val COL_NGAY_HET_HAN = "ngay_het_han"
        const val COL_SIGNATURE = "signature"

        // Bảng contracts
        const val TABLE_CONTRACTS = "contracts"
        const val COL_CONTRACT_ID = "contract_id"
        const val COL_SO_HOP_DONG = "so_hop_dong"
        const val COL_NGAY_KY = "ngay_ky"
        const val COL_BEN_A = "ben_a"
        const val COL_BEN_B_ID = "ben_b_id"

        //thong bao
        const val TABLE_NOTIFICATIONS = "notifications"

        const val COL_TITLE = "title"
        const val COL_MESSAGE = "message"
        const val COL_CREATED_AT = "created_at"

        // Temporary residence
        const val TABLE_TEMP_RESIDENCE = "temporary_residence"
        const val COL_TR_ID = "tr_id"
        const val COL_START_DATE = "start_date"
        const val COL_END_DATE = "end_date"
        const val COL_REASON = "reason"
        const val COL_STATUS = "status" // pending / received
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng residents
        val createResidentsTable = """
            CREATE TABLE $TABLE_RESIDENTS (
                $COL_RESIDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HO_TEN TEXT,
                $COL_NGAY_SINH TEXT,
                $COL_GIOI_TINH TEXT,
                $COL_CCCD TEXT UNIQUE,
                $COL_QUE_QUAN TEXT,
                $COL_NOI_THUONG_TRU TEXT,
                
                $COL_NGAY_HET_HAN TEXT,
                $COL_SIGNATURE BLOB
    
            )
        """.trimIndent()
        db.execSQL(createResidentsTable)
        // Tạo bảng users
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FULLNAME TEXT NOT NULL,
                $COL_PHONE TEXT NOT NULL,
                $COL_BIRTHDATE TEXT NOT NULL,
                $COL_USERNAME TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_ROLE TEXT NOT NULL,
                $COL_GMAIL TEXT NOT NULL,
                $COL_RESIDENT_ID INTEGER,
                FOREIGN KEY($COL_RESIDENT_ID) REFERENCES $TABLE_RESIDENTS($COL_RESIDENT_ID)
            )
        """
        db.execSQL(createUsersTable)

        // Tạo admin mặc định
        val insertAdmin = """
            INSERT INTO $TABLE_USERS (
                $COL_FULLNAME, $COL_PHONE, $COL_BIRTHDATE, 
                $COL_USERNAME, $COL_PASSWORD, $COL_ROLE, $COL_GMAIL
            ) VALUES (
                'Admin', '000000000', '01/01/2000', 
                'admin', '123', 'admin', 'admin@example.com'
            )
            
            
            
        """
        db.execSQL(insertAdmin)



        // Tạo bảng contracts
        val createContractsTable = """
            CREATE TABLE $TABLE_CONTRACTS (
                $COL_CONTRACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SO_HOP_DONG TEXT,
                $COL_NGAY_KY TEXT,
                $COL_BEN_A TEXT,
                $COL_BEN_B_ID INTEGER,
                FOREIGN KEY($COL_BEN_B_ID) REFERENCES $TABLE_RESIDENTS($COL_RESIDENT_ID)
            )
        """
        db.execSQL(createContractsTable)
        // Thêm vào trong onCreate()
        val createNotificationsTable = """
    CREATE TABLE $TABLE_NOTIFICATIONS (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_TITLE TEXT NOT NULL,
        $COL_MESSAGE TEXT NOT NULL,
        $COL_CREATED_AT TEXT NOT NULL
    )
"""
        db.execSQL(createNotificationsTable)

        val createTempResidenceTable = """
            CREATE TABLE $TABLE_TEMP_RESIDENCE (
                $COL_TR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_RESIDENT_ID INTEGER NOT NULL,
                $COL_START_DATE TEXT,
                $COL_END_DATE TEXT,
                $COL_REASON TEXT,
                $COL_STATUS TEXT DEFAULT 'pending',
                FOREIGN KEY($COL_RESIDENT_ID) REFERENCES $TABLE_RESIDENTS($COL_RESIDENT_ID)
            )
        """
        db.execSQL(createTempResidenceTable)


    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESIDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTRACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEMP_RESIDENCE")
        onCreate(db)
    }
}
