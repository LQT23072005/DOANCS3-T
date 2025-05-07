    package com.example.doancs3.Activity

    import com.example.doancs3.Api.CreateOrder
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.content.res.ColorStateList
    import android.os.Bundle
    import android.os.StrictMode
    import android.os.StrictMode.ThreadPolicy
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.doancs3.Adapter.CartAdapter
    import com.example.doancs3.Helper.TinyDB
    import com.example.doancs3.Model.ItemsModel
    import com.example.doancs3.R
    import com.example.doancs3.databinding.ActivityCartBinding
    import com.example.project1762.Helper.ChangeNumberItemsListener
    import com.example.project1762.Helper.ManagmentCart
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import org.json.JSONObject
        import vn.zalopay.sdk.Environment
        import vn.zalopay.sdk.ZaloPayError
        import vn.zalopay.sdk.ZaloPaySDK
        import vn.zalopay.sdk.listeners.PayOrderListener
    import java.text.NumberFormat
    import java.util.Locale

    class CartActivity : BaseActivity() {
        private lateinit var binding: ActivityCartBinding
        private lateinit var managerCart: ManagmentCart
        private lateinit var tinyDB: TinyDB
        private var tax: Double = 0.0
        private var selectedPaymentMethod: Int = 1 // 1: Cash, 2: ZaloPay

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityCartBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            // ZaloPay SDK Init
            ZaloPaySDK.init(2553, Environment.SANDBOX)

            managerCart = ManagmentCart(this)
            tinyDB = TinyDB(this)

            setVariable()
            initCartList()
            calculatorCart()
            loadCartFromFirebase()
        }

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

        private fun setVariable() {
            binding.apply {
                backBtn.setOnClickListener { finish() }
                button.setOnClickListener {
                    if (selectedPaymentMethod == 1) {
                        // Cash payment
                        saveOrderToFirebase()
                    } else {
                        // ZaloPay payment
                        processZaloPayPayment()
                    }
                }

                metod1.setOnClickListener {
                    selectedPaymentMethod = 1
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
                    selectedPaymentMethod = 2
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

        private fun processZaloPayPayment() {
            Log.d("ZaloPay", "Starting ZaloPay payment process")
            if (!isZaloPayAppInstalled()) {
                Log.e("ZaloPay", "ZaloPay app not installed")
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Ứng dụng ZaloPay chưa được cài đặt. Vui lòng cài đặt ZaloPay để tiếp tục.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            Log.d("ZaloPay", "ZaloPay app found, proceeding with payment")
            val amount = (managerCart.getTotalFee() + tax + 10.0).toInt()
            Log.d("ZaloPay", "Payment amount: $amount")
            val orderApi = CreateOrder()
            try {
                val data: JSONObject = orderApi.createOrder(amount.toString())
                Log.d("ZaloPay", "Full create order response: $data")
                val code: String = data.getString("return_code")
                Log.d("ZaloPay", "Return code: $code")
                if (code == "1") {
                    val token: String = data.getString("zp_trans_token")
                    Log.d("ZaloPay", "Initiating payment with token: $token")
                    Log.d("ZaloPay", "Using return URI: zalopay-sandbox://app")
                    ZaloPaySDK.getInstance().payOrder(
                        this,
                        token,
                        "demozpdk://app",
                        object : PayOrderListener {
                            override fun onPaymentSucceeded(transactionId: String, zpTransToken: String, appTransId: String) {
                                Log.d("ZaloPay", "Payment succeeded: transactionId=$transactionId, zpTransToken=$zpTransToken, appTransId=$appTransId")
                                runOnUiThread {
                                    Toast.makeText(this@CartActivity, "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                                    saveOrderToFirebase()
                                }
                            }

                            override fun onPaymentCanceled(zpTransToken: String, appTransId: String) {
                                Log.d("ZaloPay", "Payment canceled: zpTransToken=$zpTransToken, appTransId=$appTransId")
                                runOnUiThread {
                                    Toast.makeText(this@CartActivity, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onPaymentError(zaloPayError: ZaloPayError, zpTransToken: String, appTransId: String) {
                                Log.e("ZaloPay", "Payment error: ${zaloPayError.name}, message: ${zaloPayError.toString()}, zpTransToken=$zpTransToken, appTransId=$appTransId")
                                runOnUiThread {
                                    Toast.makeText(this@CartActivity, "Lỗi thanh toán: ${zaloPayError.name}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                } else {
                    val message = data.optString("return_message", "Không thể tạo đơn hàng ZaloPay")
                    Log.e("ZaloPay", "Create order failed: $message")
                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ZaloPay", "Error initiating payment: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Lỗi khi khởi tạo thanh toán ZaloPay: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun isZaloPayAppInstalled(): Boolean {
            return try {
                val packageInfo = packageManager.getPackageInfo("vn.com.vng.zalopay.sbmc", 0)
                Log.d("ZaloPay", "ZaloPay app is installed, version: ${packageInfo.versionName}")
                true
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e("ZaloPay", "ZaloPay app not found: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e("ZaloPay", "Unexpected error checking ZaloPay app: ${e.message}")
                false
            }
        }

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
            order["paymentMethod"] = if (selectedPaymentMethod == 1) "Cash" else "ZaloPay"

            orderRef.setValue(order).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Clear cart after successful order
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

        override fun onNewIntent(intent: Intent) {
            super.onNewIntent(intent)
            ZaloPaySDK.getInstance().onResult(intent)
        }
    }