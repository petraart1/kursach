package com.example.financemanager.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financemanager.R
import com.example.financemanager.databinding.FragmentStatisticsBinding
import com.example.financemanager.utils.formatCurrency
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatisticsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatisticsBinding.bind(view)
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]

        setupPieChart()
        observeData()
    }

    private fun setupPieChart() {
        binding.expenseStructureChart.apply {
            description.isEnabled = false
            setDrawEntryLabels(true)
            legend.isEnabled = true
            isRotationEnabled = true
        }
    }

    private fun observeData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser == null) {
            // Demo mode
            viewModel.getDemoStatistics()
        } else {
            // Real user data
            viewModel.getUserStatistics(currentUser.uid)
        }

        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            binding.totalIncomeValue.text = formatCurrency(stats.totalIncome)
            binding.totalExpenseValue.text = formatCurrency(stats.totalExpense)
            binding.balanceValue.text = formatCurrency(stats.balance)

            // Update pie chart
            val entries = stats.expenseByCategory.map { (category, amount) ->
                PieEntry(amount.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, getString(R.string.expense_structure))
            dataSet.colors = viewModel.getChartColors()
            
            binding.expenseStructureChart.data = PieData(dataSet)
            binding.expenseStructureChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 