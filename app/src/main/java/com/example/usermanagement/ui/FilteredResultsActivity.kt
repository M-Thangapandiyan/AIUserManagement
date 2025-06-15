package com.example.usermanagement.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usermanagement.R
import com.example.usermanagement.viewmodel.UserViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import com.example.usermanagement.data.UserDatabase
import com.example.usermanagement.repository.UserRepositoryImpl
import com.example.usermanagement.viewmodel.UserViewModelFactory
import com.example.usermanagement.ui.components.UserFilterDialog

class FilteredResultsActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var noResultsText: TextView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtered_results)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewFilteredUsers)
        noResultsText = findViewById(R.id.textViewNoResults)

        // Setup RecyclerView
        adapter = UserAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initialize repository and ViewModel
        val database = UserDatabase.getDatabase(application)
        val repository = UserRepositoryImpl(database.userDao())
        val factory = UserViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Retrieve filter from Intent and apply it
        val firstNameFilter = intent.getStringExtra(UserFilterDialog.EXTRA_FIRST_NAME_FILTER)
        if (!firstNameFilter.isNullOrEmpty()) {
            viewModel.setFirstNameFilter(firstNameFilter)
        } else {
            viewModel.clearFilters() // Clear filters if no filter is provided
        }

        // Observe filtered users using lifecycleScope
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredUsers.collect { users ->
                    if (users.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        noResultsText.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        noResultsText.visibility = View.GONE
                        adapter.submitList(users)
                    }
                }
            }
        }
    }
} 