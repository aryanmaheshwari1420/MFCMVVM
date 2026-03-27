package com.example.mfcmvvm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mfcmvvm.databinding.ActivityMainBinding
import com.example.mfcmvvm.ui.UserAdapter
import com.example.mfcmvvm.viewmodel.UiState
import com.example.mfcmvvm.viewmodel.UserViewModel
import com.example.mfcmvvm.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(this) }
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchUsers()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.fetchUsers()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchUsers(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.shimmerView.visibility = View.VISIBLE
                        binding.shimmerView.startShimmer()
                    }
                    binding.btnRetry.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.btnRetry.visibility = View.GONE
                }
                is UiState.Error -> {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.btnRetry.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Offline -> {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.btnRetry.visibility = View.VISIBLE
                    binding.tvEmptyState.text = "No internet connection. Please check your network and try again."
                    binding.tvEmptyState.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        viewModel.filteredUsers.observe(this) { users ->
            userAdapter.submitList(users)
            binding.tvEmptyState.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}