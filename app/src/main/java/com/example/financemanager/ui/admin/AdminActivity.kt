package com.example.financemanager.ui.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financemanager.R
import com.example.financemanager.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var viewModel: AdminViewModel
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.admin_panel)

        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        setupRecyclerView()
        observeUsers()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter()
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = this@AdminActivity.adapter
        }
    }

    private fun observeUsers() {
        viewModel.users.observe(this) { users ->
            adapter.submitList(users)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
} 