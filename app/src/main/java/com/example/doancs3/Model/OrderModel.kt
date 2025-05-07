package com.example.doancs3.Model

data class OrderModel(
    val timestamp: Long = 0,
    val items: List<ItemsModel> = emptyList(),
    val total: Double = 0.0,
    val status: String = ""
)