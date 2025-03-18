package com.example.doancs3.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doancs3.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class UpImageActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var editTitle: EditText
    private lateinit var buttonChooseImage: Button
    private lateinit var buttonUpload: Button
    private var selectedImageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image) // Sửa tên layout nếu cần

        // Khởi tạo các view
        imagePreview = findViewById(R.id.imagePreview)
        editTitle = findViewById(R.id.editTitle)
        buttonChooseImage = findViewById(R.id.buttonChooseImage)
        buttonUpload = findViewById(R.id.buttonUpload)

        // Khởi tạo Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Sự kiện chọn ảnh
        buttonChooseImage.setOnClickListener {
            chooseImage()
        }

        // Sự kiện upload ảnh
        buttonUpload.setOnClickListener {
            uploadImage()
        }
    }

    // Hàm chọn ảnh từ thiết bị
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    // Xử lý kết quả khi chọn ảnh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imagePreview.setImageURI(selectedImageUri) // Hiển thị ảnh lên ImageView
        }
    }

    // Hàm upload ảnh lên Firebase Storage
    private fun uploadImage() {
        val title = editTitle.text.toString().trim()

        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề!", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo tên file duy nhất bằng UUID + tiêu đề
        val fileName = "$title-${UUID.randomUUID()}.jpg"
        val imageRef = storageReference.child("images/$fileName")

        // Upload ảnh lên Firebase
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                // Lấy URL của ảnh sau khi upload thành công
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Toast.makeText(this, "Upload thành công! URL: $imageUrl", Toast.LENGTH_LONG).show()
                    // Bạn có thể lưu imageUrl này vào Firestore nếu cần
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Upload thất bại: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 100
    }
}