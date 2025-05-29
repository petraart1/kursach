package com.example.financemanager.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.data.DemoDataProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StatisticsViewModel : ViewModel() {
    private val _statistics = MutableLiveData<Statistics>()
    val statistics: LiveData<Statistics> = _statistics

    fun getDemoStatistics() {
        val demoStats = DemoDataProvider.getDemoStatistics()
        _statistics.value = demoStats
    }

    fun getUserStatistics(userId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val transactions = db.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                var totalIncome = 0.0
                var totalExpense = 0.0
                val expenseByCategory = mutableMapOf<String, Double>()

                for (doc in transactions.documents) {
                    val amount = doc.getDouble("amount") ?: 0.0
                    val type = doc.getString("type") ?: ""
                    val category = doc.getString("category") ?: ""

                    when (type) {
                        "INCOME" -> totalIncome += amount
                        "EXPENSE" -> {
                            totalExpense += amount
                            expenseByCategory[category] = (expenseByCategory[category] ?: 0.0) + amount
                        }
                    }
                }

                _statistics.value = Statistics(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = totalIncome - totalExpense,
                    expenseByCategory = expenseByCategory
                )
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun getChartColors(): List<Int> {
        return listOf(
            android.graphics.Color.rgb(64, 89, 128),
            android.graphics.Color.rgb(149, 165, 124),
            android.graphics.Color.rgb(217, 184, 162),
            android.graphics.Color.rgb(191, 134, 134),
            android.graphics.Color.rgb(179, 48, 80),
            android.graphics.Color.rgb(193, 37, 82),
            android.graphics.Color.rgb(255, 102, 0),
            android.graphics.Color.rgb(245, 199, 0),
            android.graphics.Color.rgb(106, 150, 31)
        )
    }
} 