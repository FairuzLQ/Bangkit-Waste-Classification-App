package com.example.ecohero.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecohero.adapter.HistoryAdapter
import com.example.ecohero.data.History
import com.example.ecohero.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.back.setOnClickListener {
            finish()
        }

        fetchData()
    }

    private fun fetchData() {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val accountRef = database.getReference("account/$userId/history")

        accountRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val historyList = mutableListOf<History>()
                    for (historySnapshot in snapshot.children) {
                        val historyData = historySnapshot.getValue(History::class.java)
                        historyData?.let { historyList.add(it) }
                    }
                    displayHistory(historyList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistoryActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayHistory(historyList: List<History>) {
        val adapter = HistoryAdapter(historyList)
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
    }
}
