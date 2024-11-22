package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todo.databinding.ActivityCrearCuentaBinding
import com.example.todo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class CrearCuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCuentaBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        initUI()
    }

    private fun validateData(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (email.isEmpty()) {
            Toast.makeText(
                baseContext,
                "El correo electronico es obligatorio",
                Toast.LENGTH_SHORT,
            ).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(
                baseContext,
                "La contraseña es obligatorio y debe contener al menos 6 caracteres",
                Toast.LENGTH_SHORT,
            ).show()
            return false
        }

        return true
    }

    private fun initUI() {
        binding.btnCrearCuenta.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.toString()

            if (validateData(email, password, confirmPassword))
                createAccount(email, password)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Error al crear la cuenta. Intentalo de nuevo mas tarde",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }
}