package com.example.ecohero.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecohero.data.Account
import com.example.ecohero.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("account")
        val userUid = currentUser?.uid
        val userRef = usersRef.child(userUid ?: "")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(Account::class.java)
                    binding.fullName.setText(userData?.name)
                    binding.phoneNumber.setText(userData?.phone)
                    binding.email.setText(userData?.email)
                    binding.email.isEnabled = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfile, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        binding.save.setOnClickListener {
            val updates = HashMap<String, Any>()
            updates["name"] = binding.fullName.text.toString()
            updates["phone"] = binding.phoneNumber.text.toString()

            userRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this@EditProfile, "Profile Edited", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@EditProfile, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}