package com.example.mthd.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import com.github.gcacace.signaturepad.views.SignaturePad
import database.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
import database.ResidentDAO
import model.Resident

class RegisterResidentActivity : AppCompatActivity() {

    private val REQUEST_SCAN_CCCD = 1
    private lateinit var dbHelper: DatabaseHelper

    // Hợp đồng
    private lateinit var etSoHopDong: EditText
    private lateinit var etNgayKy: EditText
    private lateinit var etBenA: EditText

    // Cư dân
    private lateinit var etHoTen: EditText
    private lateinit var etNgaySinh: EditText
    private lateinit var etGioiTinh: EditText
    private lateinit var etCCCD: EditText
    private lateinit var etNgayHetHan: EditText
    private lateinit var etNoiThuongTru: EditText
    private lateinit var etQuequan: EditText
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnClearSignature: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_resident)

        dbHelper = DatabaseHelper(this)

        // Mapping view
        etSoHopDong = findViewById(R.id.etSoHopDong)
        etNgayKy = findViewById(R.id.etNgayKy)
        etBenA = findViewById(R.id.etBenA)

        etHoTen = findViewById(R.id.etHoTen)
        etNgaySinh = findViewById(R.id.etNgaySinh)
        etGioiTinh = findViewById(R.id.etGioiTinh)
        etCCCD = findViewById(R.id.etCCCD)
        etQuequan = findViewById(R.id.etQuequan)
        etNoiThuongTru = findViewById(R.id.etNoiThuongTru)
        etNgayHetHan = findViewById(R.id.etNgayHetHan)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        signaturePad = findViewById(R.id.signaturePad)
        btnClearSignature = findViewById(R.id.btnClearSignature)
        btnClearSignature.setOnClickListener { signaturePad.clear() }

        findViewById<Button>(R.id.btnScanCCCD).setOnClickListener {
            val intent = Intent(this, ScanIDActivity::class.java)
            startActivityForResult(intent, REQUEST_SCAN_CCCD)
        }

        findViewById<Button>(R.id.btnSaveContract).setOnClickListener {
            saveDataToDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_CCCD && resultCode == RESULT_OK && data != null) {
            etHoTen.setText(data.getStringExtra("full_name"))
            etNgaySinh.setText(data.getStringExtra("dob"))
            etGioiTinh.setText(data.getStringExtra("sex"))
            etCCCD.setText(data.getStringExtra("id_number"))
            etQuequan.setText(data.getStringExtra("origin"))
            etNoiThuongTru.setText(data.getStringExtra("residence"))
            etNgayHetHan.setText(data.getStringExtra("expiry"))

            etSoHopDong.setText(data.getStringExtra("contract_number"))
            etNgayKy.setText(data.getStringExtra("contract_date"))
        }
    }

    /** Resize bitmap nếu quá lớn để tránh crash Parcel */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio: Float = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) { // landscape
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else { // portrait
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /** Lưu chữ ký vào file và trả về đường dẫn */
    private fun saveSignatureToFile(): String? {
        if (signaturePad.isEmpty) return null
        val file = File(filesDir, "signature_${System.currentTimeMillis()}.png")
        try {
            val bitmap = resizeBitmap(signaturePad.signatureBitmap) // resize trước khi lưu
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file.absolutePath
    }

    private fun saveDataToDatabase() {
        val db = dbHelper.writableDatabase

        val signaturePath = saveSignatureToFile()

        val residentValues = ContentValues().apply {
            put(DatabaseHelper.COL_HO_TEN, etHoTen.text.toString())
            put(DatabaseHelper.COL_NGAY_SINH, etNgaySinh.text.toString())
            put(DatabaseHelper.COL_GIOI_TINH, etGioiTinh.text.toString())
            put(DatabaseHelper.COL_CCCD, etCCCD.text.toString())
            put(DatabaseHelper.COL_QUE_QUAN, etQuequan.text.toString())
            put(DatabaseHelper.COL_NOI_THUONG_TRU, etNoiThuongTru.text.toString())
            put(DatabaseHelper.COL_NGAY_HET_HAN, etNgayHetHan.text.toString())
            put(DatabaseHelper.COL_SIGNATURE, signaturePath)
        }

        val residentIdLong = db.insert(DatabaseHelper.TABLE_RESIDENTS, null, residentValues)
        if (residentIdLong == -1L) {
            Toast.makeText(this, "Lưu cư dân thất bại!", Toast.LENGTH_SHORT).show()
            return
        }
        val residentId = residentIdLong.toInt()

        // --- Tạo user tự động ---
        val resident = Resident(
            fullName = etHoTen.text.toString(),
            birthDate = etNgaySinh.text.toString(),
            sex = etGioiTinh.text.toString(),
            idNumber = etCCCD.text.toString(),
            origin = etQuequan.text.toString(),
            residence = etNoiThuongTru.text.toString(),
            expiry = etNgayHetHan.text.toString(),
            signature = signaturePath?.toByteArray() // hoặc giữ nguyên path nếu bạn muốn
        )
        val residentDAO = ResidentDAO(this)
        residentDAO.addResidentWithUser(residentId, resident)

        // --- Lưu hợp đồng ---
        val contractValues = ContentValues().apply {
            put(DatabaseHelper.COL_SO_HOP_DONG, etSoHopDong.text.toString())
            put(DatabaseHelper.COL_NGAY_KY, etNgayKy.text.toString())
            put(DatabaseHelper.COL_BEN_A, etBenA.text.toString())
            put(DatabaseHelper.COL_BEN_B_ID, residentId)
        }

        val contractId = db.insert(DatabaseHelper.TABLE_CONTRACTS, null, contractValues)
        if (contractId == -1L) {
            Toast.makeText(this, "Lưu hợp đồng thất bại!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /** Ngăn hệ thống lưu bitmap SignaturePad tự động → tránh crash Parcel */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        // Không gọi super để không lưu SignaturePad
    }
}
