@file:Suppress("DEPRECATION")

package com.thefadaap.fadamarketingapp.home.ui.pharmacyprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.QuerySnapshot
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.auth.data.FadaMarketer
import com.thefadaap.fadamarketingapp.databinding.FragmentPharmacyProfileBinding
import com.thefadaap.fadamarketingapp.home.data.PharmacyData
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.Common.DATE_MONTH_YEAR_FORMAT
import com.thefadaap.fadamarketingapp.utils.enable
import com.thefadaap.fadamarketingapp.utils.getDate
import com.thefadaap.fadamarketingapp.utils.snackBar
import com.thefadaap.fadamarketingapp.utils.toast
import com.thefadaap.fadamarketingapp.utils.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PharmacyProfile : Fragment() {

    private var _binding: FragmentPharmacyProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var pharmacyId: String
    private val args: PharmacyProfileArgs by navArgs()

    private lateinit var pharmacyProfile: PharmacyData

    private lateinit var loggedInMarketer: FadaMarketer



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPharmacyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pharmacyId = args.pharmacyId
        pharmacyProfile = PharmacyData()
        loggedInMarketer = FadaMarketer()
        getMarketerDetail()

        getPharmacy(pharmacyId)
    }

    private fun getPharmacy(pharmacyId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            Common.pharmaciesCollectionRef.get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                    for (document in querySnapshot.documents) {
                        val item = document.toObject(PharmacyData::class.java)
                        if (item?.uid == pharmacyId) {
                            pharmacyProfile = item
                        }
                    }
                    setViews()
                    setActionClicks()
                    setEditClicks()

                }
        }


    }

    private fun setEditClicks() {
        with(binding) {
            ivPharmacyProfilePhoneNumberEdit.setOnClickListener {
                launchEditPhoneNumberDialog()
            }
            ivPharmacyProfileEmailEdit.setOnClickListener {
                launchEditEmailAddressDialog()
            }
            pharmacyProfileType.setOnClickListener {
                launchEditTypeDialog()
            }
        }

    }

    private fun launchEditTypeDialog() {

        val builder = layoutInflater.inflate(R.layout.edit_pharmacy_type_dialog, null)

        //builder.findViewById<TextInputLayout>(R.id.text_input_layout_edit_pharmacy_type)
        val autoCompleteViewType =
            builder.findViewById<AutoCompleteTextView>(R.id.auto_complete_text_edit_pharmacy_type)

        val btnConfirmEdit = builder.findViewById<Button>(R.id.edit_pharmacy_type_confirm_button)
        val btnCancelEdit = builder.findViewById<Button>(R.id.edit_pharmacy_type_cancel_button)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(builder)
            .setCancelable(false)
            .create()


        dialog.show()

        btnConfirmEdit.enable(false)

        var pharmacyEditedType: String

        val pharmacyTypeArray = resources.getStringArray(R.array.store_types)
        val pharmacyTypeArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, pharmacyTypeArray)
        autoCompleteViewType.setAdapter(pharmacyTypeArrayAdapter)

        autoCompleteViewType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                pharmacyEditedType = parent.getItemAtPosition(position).toString()
                btnConfirmEdit.enable(true)
            }

        btnConfirmEdit.setOnClickListener {
            pharmacyEditedType = autoCompleteViewType.text.toString().trim()
            updatePharmacyInfo("type", pharmacyEditedType, dialog)
        }

        btnCancelEdit.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun updatePharmacyInfo(data: String, pharmacyEditedValue: String, dialog: AlertDialog) {


        CoroutineScope(Dispatchers.IO).launch {
            val pharmacyRef = Common.pharmaciesCollectionRef.document(pharmacyId)

            val updateCallStatus = hashMapOf<String, Any>(
                data to pharmacyEditedValue
            )

            pharmacyRef.update(updateCallStatus)
                .addOnSuccessListener {
                    requireView().snackBar(resources.getString(R.string.pharmacy_info_updated))
                    dialog.dismiss()
                    getPharmacy(pharmacyId)
                }
                .addOnFailureListener { e ->
                    requireContext().toast(e.message.toString().trim())

                }


        }

    }


    private fun launchEditEmailAddressDialog() {

        val builder = layoutInflater.inflate(R.layout.edit_pharmacy_email_dialog, null)

        val etEditPharmacyEditEmail =
            builder.findViewById<TextInputEditText>(R.id.et_edit_pharmacy_email_text)

        val btnConfirmEdit = builder.findViewById<Button>(R.id.edit_pharmacy_email_confirm_button)
        val btnCancelEdit = builder.findViewById<Button>(R.id.edit_pharmacy_email_cancel_button)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(builder)
            .setCancelable(false)
            .create()


        dialog.show()

        btnConfirmEdit.enable(false)

        var pharmacyEditedEmail = ""

        etEditPharmacyEditEmail.addTextChangedListener {
            pharmacyEditedEmail = it.toString().trim()
            btnConfirmEdit.enable(pharmacyEditedEmail.isNotEmpty())
        }

        btnConfirmEdit.setOnClickListener {
            updatePharmacyInfo("email",pharmacyEditedEmail, dialog)
        }



        btnCancelEdit.setOnClickListener {
            dialog.dismiss()
        }


    }

    private fun launchEditPhoneNumberDialog() {

        val builder = layoutInflater.inflate(R.layout.edit_pharmacy_phone_number_dialog, null)

        val etEditPharmacyEditPhoneNumber =
            builder.findViewById<TextInputEditText>(R.id.et_edit_pharmacy_phone_number_text)

        val btnConfirmEdit = builder.findViewById<Button>(R.id.edit_pharmacy_phone_number_confirm_button)
        val btnCancelEdit = builder.findViewById<Button>(R.id.edit_pharmacy_phone_number_cancel_button)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(builder)
            .setCancelable(false)
            .create()


        dialog.show()

        btnConfirmEdit.enable(false)

        var pharmacyEditedPhoneNumber = ""

        etEditPharmacyEditPhoneNumber.addTextChangedListener {
            pharmacyEditedPhoneNumber = it.toString().trim()
            btnConfirmEdit.enable(pharmacyEditedPhoneNumber.isNotEmpty())
        }

        btnConfirmEdit.setOnClickListener {
            updatePharmacyInfo("phone", pharmacyEditedPhoneNumber, dialog)
        }



        btnCancelEdit.setOnClickListener {
            dialog.dismiss()
        }


    }

    private fun setActionClicks() {
        with(binding) {
            ivPharmacyProfilePhoneNumber.setOnClickListener {
                makeCall(pharmacyProfile.phone)
            }
            ivPharmacyProfileLocation.setOnClickListener {
                navigateMap(pharmacyProfile.location)
            }
            ivPharmacyProfileEmail.setOnClickListener {
                sendEmail(pharmacyProfile.email)
            }
            btnPharmacyProfileRemovePharmacy.setOnClickListener {
                launchConfirmDeleteDialog()
            }
        }
    }

    private fun launchConfirmDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.delete_pharmacy_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_pharmacy_dialog_message_content))
        builder.setPositiveButton(resources.getString(R.string.yes_text)) { dialog, _ ->
            // Perform any actions when the OK button is clicked
            deleteUser()
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no_text)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail(email: String) {

        if (email.isEmpty()) {
            requireContext().toast(resources.getString(R.string.no_email_address_found))
        } else {
            // TODO: Email Intent
            val pharmacyName = pharmacyProfile.name
            val marketerName = loggedInMarketer.name
            val marketerPhone = loggedInMarketer.phone

            val recipientEmail = pharmacyProfile.email
            val subject = resources.getString(R.string.email_pharmacy_subject)
            val message = resources.getString(R.string.email_pharmacy_text, pharmacyName, marketerName, marketerPhone)

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }

        }

    }

    private fun deleteUser(

    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val pharmacyRef = Common.pharmaciesCollectionRef.document(pharmacyId)

            val updateVisitStatus = hashMapOf<String, Any>(
                "isDeleted" to true,
            )

            pharmacyRef.update(updateVisitStatus)
                .addOnSuccessListener {
                    requireView().snackBar(resources.getString(R.string.pharmacy_deleted))
                    PharmacyProfileDirections.actionPharmacyProfileToPendingTasks()

                }
                .addOnFailureListener { e ->
                    requireContext().toast(e.message.toString().trim())

                }


        }

    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun navigateMap(location: String) {
        // Encode the address for the URI
        val encodedAddress = Uri.encode(location)
        // Create a URI with the address query
        val uri = Uri.parse("geo:0,0?q=$encodedAddress")
        // Create an intent with the ACTION_VIEW action and the URI
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        // Set the package name to ensure the intent opens in Google Maps app
        mapIntent.setPackage("com.google.android.apps.maps")
        // Check if there's a compatible activity to handle the intent
        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
            // Start the intent
            startActivity(mapIntent)

        }
    }

    private fun makeCall(phone: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.fromParts("tel", phone, null)
        startActivity(dialIntent)

    }

    private fun setViews() {

        with(binding) {
            pharmacyProfileName.text = pharmacyProfile.name
            pharmacyProfileDateAdded.text =
                getDate(pharmacyProfile.dateAdded.toLong(), DATE_MONTH_YEAR_FORMAT)
            pharmacyProfileType.apply {
                text =
                    pharmacyProfile.type.ifEmpty { resources.getString(R.string.change_store_type) }
                setTextColor(resources.getColor(R.color.secondary))
            }
            pharmacyProfileLocation.text = pharmacyProfile.location
            pharmacyProfileRoute.text = pharmacyProfile.route
            pharmacyProfilePhoneNumber.text = pharmacyProfile.phone
            pharmacyProfileEmail.text =
                pharmacyProfile.email.ifEmpty { resources.getString(R.string.unknown_store_email) }
            if (pharmacyProfile.hasCalled) {
                pharmacyProfileCallStatus.apply {
                    text = resources.getString(R.string.has_called_text)
                    setTextColor(resources.getColor(R.color.primary))
                }
                pharmacyProfileCallCaller.text = pharmacyProfile.callerId
                pharmacyProfileDateCalled.text = pharmacyProfile.dateCalled
                pharmacyProfileTimeCalled.text = pharmacyProfile.timeCalled
                pharmacyProfileCallComment.text = pharmacyProfile.callComment
                pharmacyProfileCallResponse.apply {
                    when (pharmacyProfile.callResponse) {
                        resources.getString(R.string.call_response_positive) -> {
                            text = pharmacyProfile.callResponse
                            setTextColor(resources.getColor(R.color.primary))
                        }

                        resources.getString(R.string.call_response_neutral) -> {
                            text = pharmacyProfile.callResponse
                            setTextColor(resources.getColor(R.color.secondary))
                        }

                        resources.getString(R.string.call_response_negative) -> {
                            text = pharmacyProfile.callResponse
                            setTextColor(resources.getColor(R.color.negative))
                        }
                    }
                }
            } else {
                pharmacyProfileCallStatus.apply {
                    text = resources.getString(R.string.has_not_called_text)
                    setTextColor(resources.getColor(R.color.secondary))
                }
                pharmacyProfileCallCaller.visible(false)
                pharmacyProfileDateCalled.visible(false)
                pharmacyProfileTimeCalled.visible(false)
                pharmacyProfileCallComment.visible(false)
                pharmacyProfileCallResponse.visible(false)
            }
            if (pharmacyProfile.hasVisited) {
                pharmacyProfileVisitStatus.apply {
                    text = resources.getString(R.string.has_visited_text)
                    setTextColor(resources.getColor(R.color.primary))
                }
                pharmacyProfileDateVisited.text = pharmacyProfile.dateVisited
                pharmacyProfileTimeVisited.text = pharmacyProfile.timeVisited
                pharmacyProfileVisitComment.text = pharmacyProfile.visitComment
                pharmacyProfileVisitResponse.apply {
                    when (pharmacyProfile.visitResponse) {
                        resources.getString(R.string.call_response_positive) -> {
                            text = pharmacyProfile.visitResponse
                            setTextColor(resources.getColor(R.color.primary))
                        }

                        resources.getString(R.string.call_response_neutral) -> {
                            text = pharmacyProfile.visitResponse
                            setTextColor(resources.getColor(R.color.secondary))
                        }

                        resources.getString(R.string.call_response_negative) -> {
                            text = pharmacyProfile.visitResponse
                            setTextColor(resources.getColor(R.color.negative))
                        }
                    }
                }
            } else {
                pharmacyProfileVisitStatus.apply {
                    text = resources.getString(R.string.has_not_visited_text)
                    setTextColor(resources.getColor(R.color.secondary))
                }
                pharmacyProfileDateVisited.visible(false)
                pharmacyProfileTimeVisited.visible(false)
                pharmacyProfileVisitComment.visible(false)
                pharmacyProfileVisitResponse.visible(false)
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
                        if (item?.uid == Common.auth.uid) {
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