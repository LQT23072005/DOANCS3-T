package com.example.doancs3.Activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.doancs3.Adapter.ListItemsAdapter
import com.example.doancs3.R
import com.example.doancs3.ViewModel.MainViewModel
import com.example.doancs3.databinding.ActivityListItemsBinding

class ListItemsActivity : BaseActivity() {
    private lateinit var binding: ActivityListItemsBinding
    private val viewModel = MainViewModel()
    private var id:String =""
    private var title:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getBundle()
        initList()
    }

    private fun initList() {
     binding.apply {
         binding.backBtn.setOnClickListener {
             finish()
         }
         progressBarList.visibility= View.VISIBLE
         viewModel.recommended.observe(this@ListItemsActivity, Observer {
             viewList.layoutManager=GridLayoutManager(this@ListItemsActivity,2)
             viewList.adapter=ListItemsAdapter(it)
             progressBarList.visibility=View.GONE
         })
         viewModel.loadFiltered(id)
     }
    }

    private fun getBundle() {
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

        binding.categoryTxt.text=title

    }
}