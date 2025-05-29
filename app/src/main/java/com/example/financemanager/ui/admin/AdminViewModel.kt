package com.example.financemanager.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financemanager.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("users").get().await()
                
                val usersList = snapshot.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        User(
                            id = doc.id,
                            email = data["email"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            isAdmin = data["isAdmin"] as? Boolean ?: false
                        )
                    }
                }
                _users.value = usersList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleAdminStatus(userId: String, isAdmin: Boolean) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .document(userId)
                    .update("isAdmin", isAdmin)
                    .await()
                
                // Reload users to reflect changes
                loadUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
} 