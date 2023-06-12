package com.thefadaap.fadamarketingapp.home.ui.addpharmacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.QuerySnapshot
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.auth.data.FadaMarketer
import com.thefadaap.fadamarketingapp.databinding.FragmentAddPharmacyBinding
import com.thefadaap.fadamarketingapp.home.data.PharmacyData
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.Common.auth
import com.thefadaap.fadamarketingapp.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddPharmacy : Fragment() {

    private var _binding: FragmentAddPharmacyBinding? = null
    private val binding get() = _binding!!

    lateinit var name: String
    lateinit var phone: String
    lateinit var location: String
    lateinit var route: String
    lateinit var email: String
    lateinit var type: String
    lateinit var enteredUserId: String
    lateinit var dateEntered: String

    lateinit var loggedInMarketer: FadaMarketer


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddPharmacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loggedInMarketer = FadaMarketer()
        getMarketerDetail()

        val routesArray = resources.getStringArray(R.array.routes)
        val routesArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, routesArray)
        binding.autoCompleteRegisterPharmacyRoute.setAdapter(routesArrayAdapter)

        val typesArray = resources.getStringArray(R.array.store_types)
        val typesArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, typesArray)
        binding.autoCompleteRegisterPharmacyType.setAdapter(typesArrayAdapter)

        binding.pharmacyRegisterButton.setOnClickListener {
            name = binding.registerPharmacyName.text.toString().trim()
            phone = binding.registerPharmacyPhoneNumber.text.toString().trim()
            location = binding.registerPharmacyLocation.text.toString().trim()
            route = binding.autoCompleteRegisterPharmacyRoute.text.toString().trim()
            email = binding.registerPharmacyEmail.text.toString().trim()
            type = binding.autoCompleteRegisterPharmacyType.text.toString().trim()
            dateEntered = System.currentTimeMillis().toString()
            enteredUserId = loggedInMarketer.uid

            performValidation()
        }

    }

    private fun performValidation() {
        with(binding) {
            if (name.isEmpty()) {
                textInputLayoutRegisterPharmacyName.error = "Pharmacy name is required"
            }
            if (phone.isEmpty()) {
                textInputLayoutRegisterPharmacyPhoneNumber.error =
                    "Pharmacy phone number is required"
            }
            if (location.isEmpty()) {
                textInputLayoutRegisterPharmacyLocation.error = "Pharmacy location is required"
            }
            if (route.isEmpty()) {
                textInputLayoutRegisterPharmacyRoute.error = "Pharmacy route is required"
            } else {
                val newPharmacy = PharmacyData(
                    uid = dateEntered,
                    name = name,
                    phone = phone,
                    location = location,
                    route = route,
                    email = email,
                    type = type,
                    enteredUserName = loggedInMarketer.name,
                    dateAdded = dateEntered
                )
                registerPharmacy(newPharmacy)
            }
        }


    }

    private fun registerPharmacy(newPharmacy: PharmacyData) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Common.pharmaciesCollectionRef.document(newPharmacy.uid).set(newPharmacy)
                    .await()
                withContext(Dispatchers.Main) {
                    val navBackToHome = AddPharmacyDirections.actionAddPharmacyToHomeLanding()
                    findNavController().navigate(navBackToHome)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    activity?.toast(e.message.toString())
                }
            }
        }


    private fun getMarketerDetail() {
        CoroutineScope(Dispatchers.IO).launch {
            Common.marketerCollectionRef
                .get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                    for (document in querySnapshot.documents) {
                        val item = document.toObject(FadaMarketer::class.java)
                        if (item?.uid == auth.uid) {
                            loggedInMarketer = item!!
                        }
                    }

                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}