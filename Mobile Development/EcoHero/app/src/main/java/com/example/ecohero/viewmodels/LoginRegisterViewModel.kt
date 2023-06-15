package com.example.ecohero.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecohero.data.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginRegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    fun registerAccount(account: Account, password: String, auth: FirebaseAuth, db: FirebaseDatabase) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(account.email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val dbRef = db.reference.child("account").child(auth.currentUser!!.uid)
                val account = Account(account.name, account.phone, account.email, auth.currentUser!!.uid, null)

                dbRef.setValue(account).addOnCompleteListener {
                    if (it.isSuccessful) {
                        _isLoading.value = false
                        _result.value = "Success"
                    } else {
                        _isLoading.value = false
                        _result.value = "Error"
                    }
                }
            } else {
                _result.value = "Error"
            }
        }
    }

    fun loginAccount(email: String, password: String, auth: FirebaseAuth) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _isLoading.value = false
                _result.value = "Success"
            } else {
                _isLoading.value = false
                _result.value = "Error"
            }
        }
    }
}