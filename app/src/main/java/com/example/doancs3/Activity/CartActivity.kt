package com.example.doancs3.Activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancs3.Adapter.CartAdapter
import com.example.doancs3.R
import com.example.doancs3.databinding.ActivityCartBinding
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.content.ContextCompat
import com.example.doancs3.Helper.TinyDB
import com.example.doancs3.Model.ItemsModel
import java.text.NumberFormat
import java.util.Locale

class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managerCart: ManagmentCart
    private lateinit var tinyDB: TinyDB
    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managerCart = ManagmentCart(this)
        tinyDB = TinyDB(this)

        setVariable()
        initCartList()
        calculatorCart()
        loadCartFromFirebase()
    }

    //khởi tạo ds giỏ hàng
    private fun initCartList() {
        binding.viewCart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewCart.adapter = CartAdapter(managerCart.getListCart(), this, object : ChangeNumberItemsListener {
            override fun onChanged() {
                calculatorCart()
                saveCartToFirebase()
            }
        })
        with(binding) {
            emptyTxt.visibility = if (managerCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView3.visibility = if (managerCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    //thiết lập các nút bấm
    private fun setVariable() {
        binding.apply {
            backBtn.setOnClickListener { finish() }
            button.setOnClickListener { saveOrderToFirebase() }

            metod1.setOnClickListener {
                metod1.setBackgroundResource(R.drawable.green_bg_selected)
                metodIc1.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.green))
                methodtitle1.setTextColor(getResources().getColor(R.color.green))
                methodSubTitle1.setTextColor(getResources().getColor(R.color.green))

                method2.setBackgroundResource(R.drawable.grey_bg_selected)
                metodIc2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.black))
                methodtitle2.setTextColor(getResources().getColor(R.color.black))
                methodSubTitle2.setTextColor(getResources().getColor(R.color.grey))
            }
            method2.setOnClickListener {
                method2.setBackgroundResource(R.drawable.green_bg_selected)
                metodIc2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.green))
                methodtitle2.setTextColor(getResources().getColor(R.color.green))
                methodSubTitle2.setTextColor(getResources().getColor(R.color.green))

                metod1.setBackgroundResource(R.drawable.grey_bg_selected)
                metodIc1.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.black))
                methodtitle1.setTextColor(getResources().getColor(R.color.black))
                methodSubTitle1.setTextColor(getResources().getColor(R.color.grey))
            }
        }
    }

    //lưu lên firebase
    private fun saveCartToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference
        val cartRef = database.child("Users").child(userId).child("cart")

        val cartItems = managerCart.getListCart()
        cartRef.setValue(cartItems).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tinyDB.putListObject("CartList", ArrayList(cartItems))
                Log.d("CartActivity", "Cart saved to Firebase: $cartItems")
            } else {
                Log.e("CartActivity", "Failed to save cart: ${task.exception?.message}")
                Toast.makeText(this, "Lỗi lưu giỏ hàng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //đọc dữ liệu từ firebase
    private fun loadCartFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference
        database.child("Users").child(userId).child("cart")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartItems = ArrayList<ItemsModel>()
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ItemsModel::class.java)
                        item?.let { cartItems.add(it) }
                    }
                    if (cartItems.isNotEmpty()) {
                        managerCart.setCartList(cartItems)
                        binding.viewCart.adapter?.notifyDataSetChanged()
                        calculatorCart()
                        Log.d("CartActivity", "Cart loaded from Firebase: $cartItems")
                    }
                    with(binding) {
                        emptyTxt.visibility = if (managerCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
                        scrollView3.visibility = if (managerCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CartActivity", "Failed to load cart: ${error.message}")
                    Toast.makeText(this@CartActivity, "Lỗi tải giỏ hàng!", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun saveOrderToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference
        val orderRef = database.child("Users").child(userId).child("orders").push()

        val order = HashMap<String, Any>()
        order["timestamp"] = System.currentTimeMillis()
        order["items"] = managerCart.getListCart()
        order["total"] = managerCart.getTotalFee() + tax + 10.0
        order["status"] = "Pending"

        orderRef.setValue(order).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                managerCart.getListCart().clear()
                tinyDB.putListObject("CartList", ArrayList())
                database.child("Users").child(userId).child("cart").removeValue()
                binding.viewCart.adapter?.notifyDataSetChanged()
                calculatorCart()
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculatorCart() {
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managerCart.getTotalFee() * percentTax) * 100) / 100.0
        val itemTotal: Double = Math.round((managerCart.getTotalFee()) * 100) / 100.0
        val total: Double = itemTotal + tax + delivery

        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0

        with(binding) {
            totalFeeTxt.text = "${formatter.format(itemTotal.toLong())}$"
            taxTxt.text = "${formatter.format(tax.toLong())}$"
            deliveryTxt.text = "${formatter.format(delivery.toLong())}$"
            totalTxt.text = "${formatter.format(total.toLong())}$"
        }
    }
}