package com.example.doancs3.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doancs3.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//Kế thừa ViewModel, giúp duy trì dữ liệu ngay cả khi Activity bị thay đổi do xoay màn hình hoặc bị hủy và tạo lại.
class MainViewModel () : ViewModel(){

    // Lấy thể hiện của Firebase Realtime Database để tương tác với cơ sở dữ liệu.
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    //_banner để chứa slidermodel và banners dùng chỉ đọc
    private val _banner = MutableLiveData<List<SliderModel>>()
    val banners:LiveData<List<SliderModel>> = _banner

   fun loadBanners(){
       //Lấy tham chiếu đến node "Banner" trong Firebase Realtime Database.
       val Ref=firebaseDatabase.getReference("Banner")

       //Lắng nghe sự thay đổi liên tục của dữ liệu trong node "Banner"
       Ref.addValueEventListener(object :ValueEventListener{

           //Xử lý dữ liệu khi có sự thay đổi
           override fun onDataChange(snapshot: DataSnapshot) {
               var lists= mutableListOf<SliderModel>()

               //Lấy tất cả các dữ liệu con trong node "Banner".
               for(childSnapshot in snapshot.children){
                   val list=childSnapshot.getValue(SliderModel::class.java)
                   if(list!=null){
                       lists.add(list)
                   }
               }
               _banner.value= lists
           }

           override fun onCancelled(error: DatabaseError) {

           }

       })
   }
}


//Code này định nghĩa MainViewModel, một lớp ViewModel trong Android
// sử dụng Firebase Realtime Database để tải danh sách banner (SliderModel)
// và lưu trữ chúng trong LiveData. Khi dữ liệu trong Firebase thay đổi,
// LiveData tự động cập nhật UI nếu nó được quan sát trong Activity hoặc Fragment.