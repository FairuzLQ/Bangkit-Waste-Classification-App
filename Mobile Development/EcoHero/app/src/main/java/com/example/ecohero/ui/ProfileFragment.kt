package com.example.ecohero.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ecohero.data.Account
import com.example.ecohero.databinding.FragmentProfileBinding
import com.example.ecohero.viewmodels.LoginPreferences
import com.example.ecohero.viewmodels.LoginViewModel
import com.example.ecohero.viewmodels.ViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("account")
        val userUid = currentUser?.uid
        val userRef = usersRef.child(userUid ?: "")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(Account::class.java)
                    binding.name.text = userData?.name
                    binding.email.text = userData?.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

        })

        binding.profile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfile::class.java))
        }

        binding.history.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        binding.about.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        binding.logout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    val pref = LoginPreferences.getInstance(requireActivity().dataStore)
                    val loginViewModel = ViewModelProvider(this@ProfileFragment, ViewModelFactory(pref))[LoginViewModel::class.java]
                    loginViewModel.setLoggedIn(false)
                    startActivity(Intent(requireContext(), OnboardingActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }
    }
}