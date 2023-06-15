package com.example.ecohero.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreferences) : ViewModel() {
    fun isLoggedIn(): LiveData<Boolean> = pref.isLoggedIn().asLiveData()

    fun setLoggedIn(isLoggedIn: Boolean) {
        viewModelScope.launch {
            pref.setLoggedIn(isLoggedIn)
        }
    }
}