package com.example.doancs3.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.doancs3.Adapter.SliderAdapter
import com.example.doancs3.Model.SliderModel
import com.example.doancs3.R
import com.example.doancs3.ViewModel.MainViewModel
import com.example.doancs3.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
    }

    private fun banners(image:List<SliderModel>){
        binding.viewPager2.adapter=SliderAdapter(image, binding.viewPager2) //gán dữ liệu vào viewPager2
        binding.viewPager2.clipToPadding=false //Đảm bảo không có cắt bớt ở viền của ViewPager2
        binding.viewPager2.clipChildren=false
        binding.viewPager2.offscreenPageLimit=3 // giới hạn số lượng trang
        binding.viewPager2.getChildAt(0).overScrollMode=RecyclerView.OVER_SCROLL_NEVER //không cho phép cuộn ra ngoài giới hạn

        val compositePageTransformer=CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40)) //tạo khoảng cách trang
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)


        //hiển thị cái dotIncator nếu lướt ảnh
        if(image.size >1){
            binding.dotIncator.visibility = View.VISIBLE
            binding.dotIncator.attachTo(binding.viewPager2)
        }
    }

    private fun initBanner(){
        binding.progressBarSlider.visibility=View.VISIBLE
        viewModel.banners.observe(this, Observer {
            banners(it)
            binding.progressBarSlider.visibility = View.GONE
        })
        viewModel.loadBanners()
    }


    //test
//    private fun initBanner() {
//        binding.progressBarSlider.visibility = View.VISIBLE
//
//        // Tạo danh sách SliderModel với tên ảnh cục bộ
//        val listOfBanners = listOf(
//            SliderModel("banner1_test"),  // image_sample1.jpg trong res/drawable
//            SliderModel("banner2_test")   // image_sample2.jpg trong res/drawable
//        )
//
//        // Cập nhật danh sách ảnh vào viewPager2
//        banners(listOfBanners)
//
//        binding.progressBarSlider.visibility = View.GONE
//    }

}