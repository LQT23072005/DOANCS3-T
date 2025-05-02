package com.example.doancs3.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.doancs3.Adapter.SliderAdapter
import com.example.doancs3.Helper.TinyDB
import com.example.doancs3.Model.SliderModel
import com.example.doancs3.ViewModel.MainViewModel
import com.example.doancs3.databinding.ActivityMainBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doancs3.Adapter.CategoryAdapter
import com.example.doancs3.Adapter.RecommendedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tinyDB: TinyDB
    private var viewModel = MainViewModel()
    private lateinit var auth: FirebaseAuth
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null || !auth.currentUser!!.isEmailVerified) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            loadProfileName()
        }

        initBanner()
        initCategory()
        initRecommended()
        initBottomMenu()
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            loadProfileName()
        }
    }

    private fun loadProfileName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("Users/$userId/profile_name")
                .get().addOnSuccessListener { snapshot ->
                    val profileName = snapshot.getValue(String::class.java) ?: tinyDB.getString("profile_name") ?: "Quang Huy"
                    tinyDB.putString("profile_name", profileName)
                    binding.textView2.text = profileName
                    Log.d(TAG, "Profile name loaded: $profileName")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to load profile name: ${e.message}")
                    binding.textView2.text = tinyDB.getString("profile_name") ?: "Quang Huy"
                    Toast.makeText(this, "Failed to load profile name", Toast.LENGTH_SHORT).show()
                }
        } else {
            binding.textView2.text = tinyDB.getString("profile_name") ?: "Quang Huy"
        }
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        binding.orderBtn.setOnClickListener { // Thêm sự kiện cho nút My Order
            startActivity(Intent(this@MainActivity, MyOrderActivity::class.java))
        }
    }

    private fun initRecommended() {
        binding.progressBarRecommend.visibility = View.VISIBLE
        binding.viewRecommendation.layoutManager = GridLayoutManager(this@MainActivity, 2)

        viewModel.recommended.observe(this, Observer { items ->
            if (items.isNullOrEmpty()) {
                Log.e(TAG, "Recommended items are empty")
                Toast.makeText(this, "No recommended items available", Toast.LENGTH_SHORT).show()
                val dummyItems = mutableListOf(
                    com.example.doancs3.Model.ItemsModel(
                        title = "Phone",
                        description = "Smartphone",
                        picUrl = arrayListOf("phone_image"),
                        model = arrayListOf("Black", "White"),
                        price = 500L,
                        rating = 4.5,
                        numberInCart = 0,
                        showRecommended = true,
                        categoryId = 1L
                    ),
                    com.example.doancs3.Model.ItemsModel(
                        title = "Laptop",
                        description = "Gaming Laptop",
                        picUrl = arrayListOf("laptop_image"),
                        model = arrayListOf("Silver", "Black"),
                        price = 1000L,
                        rating = 4.8,
                        numberInCart = 0,
                        showRecommended = true,
                        categoryId = 1L
                    )
                )
                binding.viewRecommendation.adapter = RecommendedAdapter(dummyItems)
                binding.progressBarRecommend.visibility = View.GONE
            } else {
                Log.d(TAG, "Recommended items loaded: ${items.size}")
                binding.viewRecommendation.adapter = RecommendedAdapter(items.toMutableList())
                binding.progressBarRecommend.visibility = View.GONE
            }
        })

        viewModel.loadRecommended()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        binding.viewCategory.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

        viewModel.categories.observe(this, Observer { categories ->
            if (categories.isNullOrEmpty()) {
                Log.e(TAG, "Categories are empty")
                Toast.makeText(this, "No categories available", Toast.LENGTH_SHORT).show()
                val dummyCategories = mutableListOf(
                    com.example.doancs3.Model.CategoryModel(
                        title = "Electronics",
                        id = 1,
                        picUrl = "cat_electronics"
                    ),
                    com.example.doancs3.Model.CategoryModel(
                        title = "Fashion",
                        id = 2,
                        picUrl = "cat_fashion"
                    )
                )
                binding.viewCategory.adapter = CategoryAdapter(dummyCategories)
                binding.progressBarCategory.visibility = View.GONE
            } else {
                Log.d(TAG, "Categories loaded: ${categories.size}")
                binding.viewCategory.adapter = CategoryAdapter(categories.toMutableList())
                binding.progressBarCategory.visibility = View.GONE
            }
        })
        viewModel.loadCategory()
    }

    private fun banners(image: List<SliderModel>) {
        if (image.isEmpty()) {
            Log.e(TAG, "Banner images are empty")
            Toast.makeText(this, "No banner images available", Toast.LENGTH_SHORT).show()
            binding.progressBarSlider.visibility = View.GONE
            return
        }

        Log.d(TAG, "Banner images loaded: ${image.size}")
        binding.viewPager2.adapter = SliderAdapter(image.toMutableList(), binding.viewPager2)
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)

        if (image.size > 1) {
            binding.dotIncator.visibility = View.VISIBLE
            binding.dotIncator.attachTo(binding.viewPager2)
        }
    }

    private fun initBanner() {
        binding.progressBarSlider.visibility = View.VISIBLE
        viewModel.banners.observe(this, Observer { banners ->
            if (banners.isNullOrEmpty()) {
                Log.e(TAG, "Banners are empty")
                Toast.makeText(this, "No banners available", Toast.LENGTH_SHORT).show()
                val dummyBanners = mutableListOf(
                    SliderModel("banner1_test"),
                    SliderModel("banner2_test")
                )
                banners(dummyBanners)
            } else {
                banners(banners.toMutableList())
            }
            binding.progressBarSlider.visibility = View.GONE
        })
        viewModel.loadBanners()
    }
}