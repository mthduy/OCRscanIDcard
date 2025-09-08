package com.example.mthd.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mthd.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScanIDActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: Button
    private lateinit var btnSendOCR: Button
    private lateinit var ivCapturedImage: ImageView
    private lateinit var progressOverlay: View
    private lateinit var progressBarOCR: ProgressBar

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoFile: File

    private val CAMERA_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_id)

        previewView = findViewById(R.id.previewView)
        ivCapturedImage = findViewById(R.id.ivCapturedImage)
        btnCapture = findViewById(R.id.btnCapture)
        btnSendOCR = findViewById(R.id.btnSendOCR)
        progressOverlay = findViewById(R.id.progressOverlay)
        progressBarOCR = findViewById(R.id.progressBarOCR)

        if (allPermissionsGranted()) startCamera()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

        btnCapture.setOnClickListener { takePhoto() }
        btnSendOCR.setOnClickListener {
            if (::photoFile.isInitialized && photoFile.exists()) sendImageToOCR(photoFile)
            else Toast.makeText(this, "Chưa có ảnh để gửi OCR", Toast.LENGTH_SHORT).show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetResolution(Size(1280, 720))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("ScanIDActivity", "Lỗi khi mở camera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            externalMediaDirs.first(),
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(this@ScanIDActivity, "Chụp thất bại: ${exc.message}", Toast.LENGTH_SHORT).show()
                Log.e("ScanIDActivity", "Chụp ảnh thất bại", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                this@ScanIDActivity.photoFile = photoFile
                var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                val targetRatio = 1.586f
                val width = bitmap.width
                val height = bitmap.height
                val currentRatio = width.toFloat() / height.toFloat()
                bitmap = when {
                    currentRatio > targetRatio -> {
                        val newWidth = (height * targetRatio).toInt()
                        val xOffset = (width - newWidth) / 2
                        Bitmap.createBitmap(bitmap, xOffset, 0, newWidth, height)
                    }
                    currentRatio < targetRatio -> {
                        val newHeight = (width / targetRatio).toInt()
                        val yOffset = (height - newHeight) / 2
                        Bitmap.createBitmap(bitmap, 0, yOffset, width, newHeight)
                    }
                    else -> bitmap
                }

                val resized = Bitmap.createScaledBitmap(bitmap, 1024, (1024 / targetRatio).toInt(), true)
                ivCapturedImage.setImageBitmap(Bitmap.createScaledBitmap(resized, 320, 202, true))

                val tempFile = File(cacheDir, "ocr_temp.jpg")
                FileOutputStream(tempFile).use { fos ->
                    resized.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                }
                this@ScanIDActivity.photoFile = tempFile

                Toast.makeText(this@ScanIDActivity, "Đã chụp: ${photoFile.name}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendImageToOCR(file: File) {
        Log.d("ScanIDActivity", "Gửi ảnh OCR: ${file.absolutePath}, size=${file.length()} bytes")
        progressOverlay.visibility = View.VISIBLE

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.11:8080/ocr")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressOverlay.visibility = View.GONE
                    Toast.makeText(this@ScanIDActivity, "Lỗi kết nối OCR: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("ScanIDActivity", "Lỗi kết nối OCR", e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonStr = response.body?.string()
                runOnUiThread {
                    progressOverlay.visibility = View.GONE
                    if (!response.isSuccessful) {
                        Toast.makeText(this@ScanIDActivity, "Lỗi server: $jsonStr", Toast.LENGTH_LONG).show()
                        Log.e("ScanIDActivity", "Server trả lỗi: $jsonStr")
                    } else {
                        val jsonObj = JSONObject(jsonStr ?: "{}")
                        // Tạo số hợp đồng random, ví dụ 8 chữ số
                        val contractNumber = (10000000..99999999).random().toString()

                        // Ngày ký hợp đồng là ngày hiện tại
                        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val resultIntent = Intent().apply {
                            putExtra("full_name", jsonObj.optString("full_name"))
                            putExtra("dob", jsonObj.optString("dob"))
                            putExtra("sex", jsonObj.optString("sex"))
                            putExtra("id_number", jsonObj.optString("id_number"))
                            putExtra("origin", jsonObj.optString("origin"))
                            putExtra("residence", jsonObj.optString("residence"))
                            putExtra("expiry", jsonObj.optString("expiry"))
                            putExtra("contract_number", contractNumber)
                            putExtra("contract_date", currentDate)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
