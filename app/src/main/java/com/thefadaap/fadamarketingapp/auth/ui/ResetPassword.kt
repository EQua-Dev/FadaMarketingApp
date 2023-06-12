package com.thefadaap.fadamarketingapp.auth.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.databinding.FragmentResetPasswordBinding
import com.thefadaap.fadamarketingapp.utils.Common.IS_FIRST_SIGN_IN
import com.thefadaap.fadamarketingapp.utils.Common.isFirstSignIn
import com.thefadaap.fadamarketingapp.utils.PreferenceStore
import com.thefadaap.fadamarketingapp.utils.toast
import kotlinx.coroutines.launch

class ResetPassword : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var userEmail: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accountForgotPasswordBtnResetPassword.setOnClickListener {
            userEmail = binding.forgotPasswordEmail.text.toString()


            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully

                        //val preferenceStore = PreferenceStore(requireContext())
                        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean(IS_FIRST_SIGN_IN, false)

//                        lifecycleScope.launch {
//                            preferenceStore.updateSession(false)
//                        }
                        requireContext().toast(resources.getString(R.string.reset_password_instruction))
                        val navToSignIn = ResetPasswordDirections.actionResetPasswordToSignIn()
                        findNavController().navigate(navToSignIn)
                    } else {
                        // Error occurred while sending password reset email
                        requireContext().toast(resources.getString(R.string.reset_password_error))
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}