package com.example.doancs3.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.doancs3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtlogin: EditText
    private lateinit var edtpass: EditText
    private lateinit var btnlogin: Button
    private lateinit var btnChange: ImageButton


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)


        edtlogin = findViewById(R.id.edtsignup_email)
        edtpass = findViewById(R.id.edtpassword)
        btnlogin = findViewById(R.id.btn_SignUp)
        btnChange = findViewById(R.id.btn_Change)
        mAuth = FirebaseAuth.getInstance()


        btnlogin.setOnClickListener {
            val login = edtlogin.text.toString().trim()
            val pass = edtpass.text.toString().trim()


            if (login.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Thực hiện đăng ký
            mAuth.createUserWithEmailAndPassword(login, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Main", "CreateUserWithEmail:success")
                        val user: FirebaseUser? = mAuth.currentUser
                        Toast.makeText(this, "ok rồi cu", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Main", "CreateUserWithEmail:failed", task.exception)
                        val errorMessage = task.exception?.message ?: "tạch rồi cu"
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnChange.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Ngăn quay lại LoginActivity
        }
    }
}