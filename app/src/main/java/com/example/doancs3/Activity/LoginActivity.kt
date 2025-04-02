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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doancs3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var edtlogin: EditText
    private lateinit var edtpass: EditText
    private lateinit var btnlogin: Button
    private lateinit var btnChange: ImageButton
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        edtlogin = findViewById(R.id.edtemail_login)
        edtpass = findViewById(R.id.edtpassword_login)
        btnlogin = findViewById(R.id.btn_Login)
        btnChange = findViewById(R.id.btn_Change_signUp)
        mAuth = FirebaseAuth.getInstance()

        btnlogin.setOnClickListener {
            var login = edtlogin.text.trim()
            var pass = edtpass.text.trim()


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


            mAuth.signInWithEmailAndPassword(login.toString(), pass.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "signInWithEmail:success")
                        val user: FirebaseUser? = mAuth.currentUser
                        Toast.makeText(this, "ok rồi cu", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Ngăn quay lại LoginActivity
                    } else {
                        Log.d("Login", "signInWithEmail:failed", task.exception)
                        val errorMessage = task.exception?.message ?: "tạch rồi cu"
                        Toast.makeText(this, "sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnChange.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish() // Ngăn quay lại LoginActivity
        }
    }
}