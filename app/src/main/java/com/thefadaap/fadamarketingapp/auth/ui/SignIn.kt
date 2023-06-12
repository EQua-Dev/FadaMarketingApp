package com.thefadaap.fadamarketingapp.auth.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thefadaap.fadamarketingapp.databinding.FragmentSignInBinding
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.Common.IS_FIRST_SIGN_IN
import com.thefadaap.fadamarketingapp.utils.enable
import com.thefadaap.fadamarketingapp.utils.hideProgress
import com.thefadaap.fadamarketingapp.utils.showProgress
import com.thefadaap.fadamarketingapp.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignIn : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String
    private lateinit var password: String

    private val TAG = "SignIn"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInPassword.addTextChangedListener {
            email = binding.signInEmail.text.toString().trim()
            password = it.toString().trim()
            binding.accountLogInBtnLogin.enable(email.isNotEmpty() && password.isNotEmpty())
        }

        binding.accountLogInBtnLogin.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.textInputLayoutSignInEmail.error =
                    "Enter valid email"
            } else {
                binding.textInputLayoutSignInEmail.error = null
                signIn(email, password)

            }
        }

    }

    private fun signIn(email: String, password: String) {
        requireContext().showProgress()

        email.let { Common.auth.signInWithEmailAndPassword(it, password) }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            withContext(Dispatchers.Main) {
                                hideProgress()
                                //val preferenceStore = PreferenceStore(requireContext())
                                val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                val isFirstSignIn = sharedPreferences.getBoolean(IS_FIRST_SIGN_IN, true)
                                if (!isFirstSignIn){
                                    val navToResetPassword =
                                        SignInDirections.actionSignInToResetPassword()
                                    findNavController().navigate(navToResetPassword)
                                }
                                else{
                                    val navToLanding =
                                        SignInDirections.actionSignInToHomeLanding()
                                    findNavController().navigate(navToLanding)
                                }

                            }


                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                hideProgress()
                                requireActivity().toast(e.message.toString())
                                Log.d(TAG, "signIn: ${e.message.toString()}")
                            }
                        }
                    }

                } else {
                    hideProgress()
                    activity?.toast(it.exception?.message.toString())
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}