package com.example.doancs3.Activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager  // Thêm dòng này
import com.example.doancs3.Adapter.CartAdapter
import com.example.doancs3.R
import com.example.doancs3.databinding.ActivityCartBinding
import com.example.doancs3.databinding.ActivityListItemsBinding
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart


class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managerCart: ManagmentCart
    private var tax:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managerCart= ManagmentCart(this)

        setVariable()
        initCartList()
        calculatorCart()
        }

    private fun initCartList(){
        binding.viewCart.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.viewCart.adapter =
            CartAdapter(managerCart.getListCart(),this,object :ChangeNumberItemsListener{
            override fun onChanged() {
                calculatorCart()
            }

        })
        with(binding){
            emptyTxt.visibility =
                if(managerCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView3.visibility =
                if(managerCart.getListCart().isEmpty())View.GONE else View.VISIBLE
        }
    }

    private fun setVariable(){
        binding.apply {
            backBtn.setOnClickListener{
                finish()
            }

            metod1.setOnClickListener{
                metod1.setBackgroundResource(R.drawable.green_bg_selected)
                metodIc1.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.green))
                methodtitle1.setTextColor(getResources().getColor(R.color.green))
                methodSubTitle1.setTextColor(getResources().getColor(R.color.green))


                method2.setBackgroundResource(R.drawable.grey_bg_selected)
                metodIc2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity, R.color.black))
                methodtitle2.setTextColor(getResources().getColor(R.color.black))
                methodSubTitle2.setTextColor(getResources().getColor(R.color.grey))

            }
            method2.setOnClickListener{
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

    private fun calculatorCart(){
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managerCart.getTotalFee() * percentTax)* 100) /100.0
        val total =Math.round((managerCart.getTotalFee()* tax* delivery)*100)/100
        val itemTotal = Math.round(managerCart.getTotalFee()*100)/100

        with(binding){
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text="$$tax"
            deliveryTxt.text="$$delivery"
            totalTxt.text="$total"
        }
    }
}