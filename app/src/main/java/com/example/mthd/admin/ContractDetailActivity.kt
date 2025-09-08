package com.example.mthd.admin

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.mthd.R
import database.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// iText imports
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter

class ContractDetailActivity : AppCompatActivity() {

    private lateinit var tvContractNumber: TextView
    private lateinit var tvContractDate: TextView
    private lateinit var tvBenA: TextView
    private lateinit var tvBenB: TextView
    private lateinit var tvDob: TextView
    private lateinit var tvSex: TextView
    private lateinit var tvCCCD: TextView
    private lateinit var tvQuequan: TextView
    private lateinit var tvNoiThuongTru: TextView
    private lateinit var tvExpiry: TextView
    private lateinit var btnEditContract: Button
    private lateinit var btnExportPdf: Button
    private lateinit var btnBack: ImageButton

    private lateinit var btnDelete:Button

    private lateinit var dbHelper: DatabaseHelper

    private var contractId: Long = -1L
    private var residentId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_detail)

        btnDelete = findViewById(R.id.btnDelete)
        tvContractNumber = findViewById(R.id.tvContractNumber)
        tvContractDate = findViewById(R.id.tvContractDate)
        tvBenA = findViewById(R.id.tvBenA)
        tvBenB = findViewById(R.id.tvBenB)
        tvDob = findViewById(R.id.tvDob)
        tvSex = findViewById(R.id.tvSex)
        tvCCCD = findViewById(R.id.tvCCCD)
        tvQuequan = findViewById(R.id.tvQuequan)
        tvNoiThuongTru = findViewById(R.id.tvNoiThuongTru)
        tvExpiry = findViewById(R.id.tvExpiry)
        btnEditContract = findViewById(R.id.btnEditContract)
        btnExportPdf = findViewById(R.id.btnExportPdf)
        dbHelper = DatabaseHelper(this)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // quay về màn hình trước (ví dụ ContractListActivity)
        }



        contractId = intent.getLongExtra("contract_id", -1)
        if (contractId != -1L) {
            loadContractDetail(contractId)
        } else {
            Toast.makeText(this, "Không tìm thấy hợp đồng", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnEditContract.setOnClickListener {
            showEditDialog()
        }
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xóa hợp đồng")
                .setMessage("Bạn có chắc muốn xóa hợp đồng này không?")
                .setPositiveButton("Xóa") { _, _ ->
                    deleteContract(contractId, residentId)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        btnExportPdf.setOnClickListener {
            exportPdf()
        }
        val role = intent.getStringExtra("role") ?: "user"

        if (role == "user") {
            btnEditContract.visibility = View.GONE
            btnDelete.visibility = View.GONE
        }
        else {
            // Admin nhìn thấy đầy đủ
            btnEditContract.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
        }
    }

    private fun loadContractDetail(contractIdParam: Long) {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            // lấy thêm c.contract_id và r.resident_id để dùng khi update
            "SELECT c.${DatabaseHelper.COL_CONTRACT_ID}, " +
                    "c.${DatabaseHelper.COL_SO_HOP_DONG}, c.${DatabaseHelper.COL_NGAY_KY}, c.${DatabaseHelper.COL_BEN_A}, " +
                    "r.${DatabaseHelper.COL_RESIDENT_ID}, r.${DatabaseHelper.COL_HO_TEN}, r.${DatabaseHelper.COL_NGAY_SINH}, r.${DatabaseHelper.COL_GIOI_TINH}, " +
                    "r.${DatabaseHelper.COL_CCCD}, r.${DatabaseHelper.COL_QUE_QUAN}, r.${DatabaseHelper.COL_NOI_THUONG_TRU}, r.${DatabaseHelper.COL_NGAY_HET_HAN} " +
                    "FROM ${DatabaseHelper.TABLE_CONTRACTS} c " +
                    "JOIN ${DatabaseHelper.TABLE_RESIDENTS} r ON c.${DatabaseHelper.COL_BEN_B_ID}=r.${DatabaseHelper.COL_RESIDENT_ID} " +
                    "WHERE c.${DatabaseHelper.COL_CONTRACT_ID} = ?",
            arrayOf(contractIdParam.toString())
        )

        if (cursor.moveToFirst()) {
            // indexes:
            // 0: contract_id
            // 1: so_hop_dong
            // 2: ngay_ky
            // 3: ben_a
            // 4: resident_id
            // 5: ho_ten
            // 6: ngay_sinh
            // 7: gioi_tinh
            // 8: cccd
            // 9: que_quan
            // 10: noi_thuong_tru
            // 11: ngay_het_han

            this.contractId = cursor.getLong(0)
            this.residentId = cursor.getLong(4)

            tvContractNumber.text = cursor.getString(1) ?: ""
            tvContractDate.text = cursor.getString(2) ?: ""
            tvBenA.text = cursor.getString(3) ?: ""
            tvBenB.text = cursor.getString(5) ?: ""
            tvDob.text = cursor.getString(6) ?: ""
            tvSex.text = cursor.getString(7) ?: ""
            tvCCCD.text = cursor.getString(8) ?: ""
            tvQuequan.text = cursor.getString(9) ?: ""
            tvNoiThuongTru.text = cursor.getString(10) ?: ""
            tvExpiry.text = cursor.getString(11) ?: ""
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu hợp đồng", Toast.LENGTH_SHORT).show()
            finish()
        }
        cursor.close()
    }

    private fun deleteContract(contractId: Long, residentId: Long) {
        try {
            val db = dbHelper.writableDatabase

            // Xóa hợp đồng
            db.delete(
                DatabaseHelper.TABLE_CONTRACTS,
                "${DatabaseHelper.COL_CONTRACT_ID} = ?",
                arrayOf(contractId.toString())
            )

            // Nếu muốn xóa luôn cư dân liên quan (chỉ khi cư dân không có hợp đồng khác)
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_CONTRACTS} WHERE ${DatabaseHelper.COL_BEN_B_ID} = ?",
                arrayOf(residentId.toString())
            )
            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()

            if (count == 0) {
                db.delete(
                    DatabaseHelper.TABLE_RESIDENTS,
                    "${DatabaseHelper.COL_RESIDENT_ID} = ?",
                    arrayOf(residentId.toString())
                )
            }

            db.close()

            Toast.makeText(this, "Đã xóa hợp đồng", Toast.LENGTH_SHORT).show()
            finish() // quay về màn hình trước
        } catch (e: Exception) {
            Log.e("ContractDetail", "Lỗi xóa: ", e)
            Toast.makeText(this, "Lỗi khi xóa: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun showEditDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_edit_contract, null)

        val etSoHopDong = dialogView.findViewById<EditText>(R.id.etSoHopDong)
        val etNgayKy = dialogView.findViewById<EditText>(R.id.etNgayKy)
        val etBenA = dialogView.findViewById<EditText>(R.id.etBenA)
        val etHoTen = dialogView.findViewById<EditText>(R.id.etHoTen)
        val etNgaySinh = dialogView.findViewById<EditText>(R.id.etNgaySinh)
        val etGioiTinh = dialogView.findViewById<EditText>(R.id.etGioiTinh)
        val etCCCD = dialogView.findViewById<EditText>(R.id.etCCCD)
        val etQueQuan = dialogView.findViewById<EditText>(R.id.etQueQuan)
        val etNoiThuongTru = dialogView.findViewById<EditText>(R.id.etNoiThuongTru)
        val etNgayHetHan = dialogView.findViewById<EditText>(R.id.etNgayHetHan)

        // điền sẵn giá trị hiện tại
        etSoHopDong.setText(tvContractNumber.text.toString())
        etNgayKy.setText(tvContractDate.text.toString())
        etBenA.setText(tvBenA.text.toString())
        etHoTen.setText(tvBenB.text.toString())
        etNgaySinh.setText(tvDob.text.toString())
        etGioiTinh.setText(tvSex.text.toString())
        etCCCD.setText(tvCCCD.text.toString())
        etQueQuan.setText(tvQuequan.text.toString())
        etNoiThuongTru.setText(tvNoiThuongTru.text.toString())
        etNgayHetHan.setText(tvExpiry.text.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle("Chỉnh sửa hợp đồng")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                val newSoHopDong = etSoHopDong.text.toString().trim()
                val newNgayKy = etNgayKy.text.toString().trim()
                val newBenA = etBenA.text.toString().trim()
                val newHoTen = etHoTen.text.toString().trim()
                val newNgaySinh = etNgaySinh.text.toString().trim()
                val newGioiTinh = etGioiTinh.text.toString().trim()
                val newCCCD = etCCCD.text.toString().trim()
                val newQueQuan = etQueQuan.text.toString().trim()
                val newNoiThuongTru = etNoiThuongTru.text.toString().trim()
                val newNgayHetHan = etNgayHetHan.text.toString().trim()

                // update DB
                updateContractAndResident(
                    contractId,
                    residentId,
                    newSoHopDong, newNgayKy, newBenA,
                    newHoTen, newNgaySinh, newGioiTinh,
                    newCCCD, newQueQuan, newNoiThuongTru, newNgayHetHan
                )
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }

    private fun updateContractAndResident(
        contractId: Long,
        residentId: Long,
        soHopDong: String,
        ngayKy: String,
        benA: String,
        hoTen: String,
        ngaySinh: String,
        gioiTinh: String,
        cccd: String,
        queQuan: String,
        noiThuongTru: String,
        ngayHetHan: String
    ) {
        try {
            val db = dbHelper.writableDatabase

            val contractValues = ContentValues().apply {
                put(DatabaseHelper.COL_SO_HOP_DONG, soHopDong)
                put(DatabaseHelper.COL_NGAY_KY, ngayKy)
                put(DatabaseHelper.COL_BEN_A, benA)
            }
            db.update(
                DatabaseHelper.TABLE_CONTRACTS,
                contractValues,
                "${DatabaseHelper.COL_CONTRACT_ID} = ?",
                arrayOf(contractId.toString())
            )

            val residentValues = ContentValues().apply {
                put(DatabaseHelper.COL_HO_TEN, hoTen)
                put(DatabaseHelper.COL_NGAY_SINH, ngaySinh)
                put(DatabaseHelper.COL_GIOI_TINH, gioiTinh)
                put(DatabaseHelper.COL_CCCD, cccd)
                put(DatabaseHelper.COL_QUE_QUAN, queQuan)
                put(DatabaseHelper.COL_NOI_THUONG_TRU, noiThuongTru)
                put(DatabaseHelper.COL_NGAY_HET_HAN, ngayHetHan)
            }
            db.update(
                DatabaseHelper.TABLE_RESIDENTS,
                residentValues,
                "${DatabaseHelper.COL_RESIDENT_ID} = ?",
                arrayOf(residentId.toString())
            )

            // refresh UI
            loadContractDetail(contractId)
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ContractDetail", "Lỗi update: ", e)
            Toast.makeText(this, "Lỗi lưu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun exportPdf() {
        try {
            val docsFolder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (docsFolder != null && !docsFolder.exists()) docsFolder.mkdirs()

            val contractNumber = tvContractNumber.text.toString().ifBlank { "hopdong" }
            val fileName = "HopDong_${contractNumber}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val pdfFile = File(docsFolder, fileName)

            val document = Document(PageSize.A4, 36f, 36f, 36f, 36f)
            val outStream = FileOutputStream(pdfFile)
            PdfWriter.getInstance(document, outStream)
            document.open()

            // ===== Load font Times New Roman =====
            val fontPath = copyFontToCacheIfNeeded("TimesNewRoman.ttf")
            val baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            val normalFont = Font(baseFont, 12f)
            val boldFont = Font(baseFont, 12f, Font.BOLD)
            val titleFont = Font(baseFont, 16f, Font.BOLD)

            // ===== Quốc hiệu =====
            val header = Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n", boldFont)
            header.alignment = Element.ALIGN_CENTER
            document.add(header)

            // Lấy ngày ký từ TextView
            val contractDate = tvContractDate.text.toString().ifBlank { "....../....../......" }

// Hiển thị căn phải
            val dateParagraph = Paragraph("TP Hồ Chí Minh, ngày $contractDate\n\n", normalFont)
            dateParagraph.alignment = Element.ALIGN_RIGHT
            document.add(dateParagraph)


            // ===== Tiêu đề =====
            val title = Paragraph("HỢP ĐỒNG THUÊ CĂN HỘ\n\n", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            // ===== Nội dung hợp đồng (theo file Word) =====
            val content = """
- Căn cứ Bộ luật Dân sự số 91/2015/QH13 ngày 24/11/2015;
- Căn cứ vào Luật Thương mại số 36/2005/QH11 ngày 14 tháng 06 năm 2005;
- Căn cứ vào nhu cầu và sự thỏa thuận của các bên tham gia Hợp đồng;

Hôm nay, ngày ${tvContractDate.text}, các Bên gồm:

BÊN CHO THUÊ (Bên A): 
Ông/Bà: Mai Văn Bảy
CMND/CCCD số: 083387378221
Cơ quan cấp: Cục Trưởng Cục Cảnh Sát Quản Lý Hành Chính Về Trật Tự Xã Hội
Nơi ĐKTT: Hiệp Hưng, Phụng Hiệp, Hậu Giang

BÊN THUÊ (Bên B):
Ông/Bà: ${tvBenB.text}
Ngày sinh: ${tvDob.text}
Giới tính: ${tvSex.text}
CMND/CCCD số: ${tvCCCD.text}
Cơ quan cấp: Cục Trưởng Cục Cảnh Sát Quản Lý Hành Chính Về Trật Tự Xã Hội
Quê quán: ${tvQuequan.text}
Nơi ĐKTT: ${tvNoiThuongTru.text}
Ngày hết hạn CCCD: ${tvExpiry.text}

Bên A và Bên B sau đây gọi chung là “Hai Bên” hoặc “Các Bên”.
Sau khi thảo luận, Hai Bên thống nhất đi đến ký kết Hợp đồng thuê căn hộ (“Hợp Đồng”) với các điều khoản và điều kiện dưới đây:

Điều 1. Nhà ở và các tài sản cho thuê kèm theo nhà ở:
1.1. Bên A đồng ý cho Bên B thuê căn hộ tại địa chỉ 845/2 Nguyễn Bình, Nhơn Đức, Nhà Bè, TP HCM để sử dụng làm nơi để ở.
Diện tích căn nhà: 80m2;
1.2. Bên A cam kết quyền sử dụng đất và căn nhà gắn liền trên đất là tài sản sở hữu hợp pháp của Bên A. 

Điều 2. Bàn giao và sử dụng diện tích thuê:
2.1. Thời điểm bàn giao: ....../....../......
2.2. Bên B được toàn quyền sử dụng tài sản thuê từ ngày bàn giao.

Điều 3. Thời hạn thuê:
3.1. Thời hạn thuê: 01 năm kể từ ngày bàn giao.
3.2. Hết hạn thuê nếu Bên B có nhu cầu thì Bên A phải ưu tiên cho Bên B tiếp tục thuê.

Điều 4. Đặt cọc tiền thuê nhà:
4.1. Bên B đặt cọc cho Bên A: 12.000.000 VNĐ (bằng chữ: mười hai triệu đồng).
4.2. Nếu Bên B đơn phương chấm dứt hợp đồng mà không báo trước thì mất cọc. Nếu Bên A đơn phương thì phải hoàn lại và bồi thường gấp đôi.
4.3. Tiền cọc không được dùng để thanh toán tiền thuê.
4.4. Khi kết thúc hợp đồng, Bên A hoàn trả tiền cọc sau khi trừ thiệt hại (nếu có).

Điều 5. Tiền thuê nhà:
5.1. 12.000.000 VNĐ/tháng (bằng chữ: mười hai triệu đồng).
5.2. Không bao gồm điện, nước, vệ sinh... 

Điều 6. Phương thức thanh toán:
Thanh toán 01 tháng/lần vào ngày 05 hàng tháng. 
Các chi phí khác do Bên B tự thanh toán.

Điều 7. Quyền và nghĩa vụ của Bên A:
- Yêu cầu Bên B trả tiền đúng hạn;
- Bàn giao tài sản đúng thời gian;
- Đảm bảo cho thuê đúng quy định pháp luật;
- Không xâm phạm tài sản Bên B.

Điều 8. Quyền và nghĩa vụ của Bên B:
- Nhận bàn giao căn hộ đúng thỏa thuận;
- Sử dụng đúng mục đích, giữ gìn nhà;
- Thanh toán tiền thuê đúng hạn;
- Trả lại nhà khi hết hợp đồng.

Điều 9. Đơn phương chấm dứt hợp đồng:
Bên muốn chấm dứt phải báo trước 30 ngày. Nếu không thì bồi thường.

Điều 10. Điều khoản thi hành:
- Hợp đồng có hiệu lực từ ngày ký;
- Hợp đồng lập thành 02 bản, mỗi bên giữ 01 bản.


        """.trimIndent()

            document.add(Paragraph(content, normalFont))

// ===== Chữ ký 2 bên =====
            document.add(Paragraph("\n\n")) // cách ra cho đẹp

            val signatureTable = com.itextpdf.text.pdf.PdfPTable(2)
            signatureTable.widthPercentage = 100f
            signatureTable.setWidths(floatArrayOf(1f, 1f))

// ===== Bên A =====
            // ===== Bên A =====
            val cellA = com.itextpdf.text.pdf.PdfPCell()
            cellA.border = com.itextpdf.text.Rectangle.NO_BORDER
            cellA.horizontalAlignment = Element.ALIGN_CENTER
            cellA.addElement(Paragraph("BÊN CHO THUÊ", normalFont))
            cellA.addElement(Paragraph("(ký, ghi rõ họ tên)", normalFont))
            cellA.addElement(Paragraph("\n")) // chừa khoảng trống cho chữ ký

// thêm chữ ký admin (nếu có)
            try {
                val signatureStream = assets.open("signatures/img.png")
                val signatureFile = File(cacheDir, "admin_signature.png")
                signatureStream.use { input ->
                    FileOutputStream(signatureFile).use { output -> input.copyTo(output) }
                }
                val signatureImage = com.itextpdf.text.Image.getInstance(signatureFile.absolutePath)
                signatureImage.scaleToFit(150f, 50f)
                cellA.addElement(signatureImage)
            } catch (e: Exception) {
                e.printStackTrace()
                cellA.addElement(Paragraph("(chưa có chữ ký)", normalFont))
            }

// **thêm tên cố định Bên A**
            cellA.addElement(Paragraph("MAI VĂN BẢY", normalFont))

            signatureTable.addCell(cellA)


// ===== Bên B =====
            // Bên B
            val cellB = com.itextpdf.text.pdf.PdfPCell()
            cellB.border = com.itextpdf.text.Rectangle.NO_BORDER
            cellB.horizontalAlignment = Element.ALIGN_CENTER

            cellB.addElement(Paragraph("BÊN THUÊ", normalFont))
            cellB.addElement(Paragraph("(ký, ghi rõ họ tên)", normalFont))
            cellB.addElement(Paragraph("\n")) // khoảng trống trước chữ ký

// Thêm chữ ký căn trái
            val cursor = dbHelper.readableDatabase.query(
                DatabaseHelper.TABLE_RESIDENTS,
                arrayOf(DatabaseHelper.COL_SIGNATURE, DatabaseHelper.COL_HO_TEN),
                "${DatabaseHelper.COL_RESIDENT_ID} = ?",
                arrayOf(residentId.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                val signaturePath = cursor.getString(0)
                if (!signaturePath.isNullOrEmpty()) {
                    val signatureImage = com.itextpdf.text.Image.getInstance(signaturePath)
                    signatureImage.scaleToFit(150f, 50f)
                    signatureImage.alignment = Element.ALIGN_LEFT // căn trái
                    cellB.addElement(signatureImage) // chỉ chữ ký căn trái
                } else {
                    cellB.addElement(Paragraph("(chưa có chữ ký)", normalFont))
                }

                // Tên cư dân căn giữa, tách hẳn ra
                val nameParagraph = Paragraph(cursor.getString(1) ?: "", normalFont)
                cellB.addElement(Paragraph("\n")) // khoảng trống giữa chữ ký và tên
                cellB.addElement(nameParagraph)
            }
            cursor.close()


            signatureTable.addCell(cellB)


            document.add(signatureTable)


// ===== Đóng file =====
            document.close()
            outStream.close()


            // ===== Mở file PDF =====
            val pdfUri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", pdfFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)

        } catch (e: Exception) {
            Log.e("ContractDetail", "Lỗi xuất PDF", e)
            Toast.makeText(this, "Lỗi xuất PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



    // Copy font từ assets/fonts/latin.ttf vào cache để iText load bằng đường dẫn tuyệt đối
    private fun copyFontToCacheIfNeeded(fileName: String): String {
        val outFile = File(cacheDir, fileName)
        try {
            if (!outFile.exists()) {
                Log.d("ContractDetail", "Đang copy font từ assets/fonts/$fileName vào ${outFile.absolutePath}")
                assets.open("fonts/$fileName").use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                Log.d("ContractDetail", "Font đã tồn tại trong cache: ${outFile.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("ContractDetail", "Không tìm thấy font trong assets: fonts/$fileName", e)
        }
        return outFile.absolutePath
    }

}
