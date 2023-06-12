@file:Suppress("NAME_SHADOWING")

package com.thefadaap.fadamarketingapp.home.ui.pharmaciestocall

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.QuerySnapshot
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.auth.data.FadaMarketer
import com.thefadaap.fadamarketingapp.databinding.FragmentPharmaciesToCallBinding
import com.thefadaap.fadamarketingapp.home.data.PharmacyData
import com.thefadaap.fadamarketingapp.home.ui.HomeLandingDirections
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.Common.DATE_MONTH_YEAR_FORMAT
import com.thefadaap.fadamarketingapp.utils.getDate
import com.thefadaap.fadamarketingapp.utils.snackBar
import com.thefadaap.fadamarketingapp.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PharmaciesToCall : Fragment() {

    private var _binding: FragmentPharmaciesToCallBinding? = null
    private val binding get() = _binding!!

    private lateinit var loggedInMarketer: FadaMarketer


    private var pharmaciesToCallAdapter: FirestoreRecyclerAdapter<PharmacyData, PharmaciesToCallAdapter>? =
        null

    private val calendar = Calendar.getInstance()

    val TAG = "Pharmacies To Call"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPharmaciesToCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: Arrived here")

        loggedInMarketer = FadaMarketer()
        getMarketerDetail()

        getPharmaciesToCall()
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            rvPharmaciesToCall.layoutManager = layoutManager
            rvPharmaciesToCall.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )
        }

    }

    private fun getPharmaciesToCall() {

        val pharmaciesToCall =
            Common.pharmaciesCollectionRef.whereEqualTo("hasCalled", false).whereEqualTo("isDeleted", false)
        val options = FirestoreRecyclerOptions.Builder<PharmacyData>()
            .setQuery(pharmaciesToCall, PharmacyData::class.java).build()
        Log.d(TAG, "getPharmaciesToCall: $pharmaciesToCall")
        try {
            pharmaciesToCallAdapter = object :
                FirestoreRecyclerAdapter<PharmacyData, PharmaciesToCallAdapter>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PharmaciesToCallAdapter {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.pharmacies_to_call_item_layout, parent, false)
                    return PharmaciesToCallAdapter(itemView)
                }

                override fun onBindViewHolder(
                    holder: PharmaciesToCallAdapter,
                    position: Int,
                    model: PharmacyData
                ) {
                    Log.d(TAG, "onBindViewHolder: $model")

                    holder.pharmacyDateAdded.text =
                        getDate(model.dateAdded.toLong(), DATE_MONTH_YEAR_FORMAT)
                    holder.pharmacyName.text = model.name
                    holder.pharmacyRoute.text = model.route
                    holder.pharmacyType.text = model.type

                    holder.pharmacyIHaveCalled.setOnClickListener {
                        launchIHaveCalledDialog(model)
                    }

                    holder.itemView.setOnClickListener {
                        val navToPharmacyProfile =
                            HomeLandingDirections.actionHomeLandingToPharmacyProfile(model.uid)//.actionPendingTasksToPharmacyProfile(model.uid)
                        findNavController().navigate(navToPharmacyProfile)
                    }

                }

            }

        } catch (e: Exception) {
            requireActivity().toast(e.message.toString())
        }
        pharmaciesToCallAdapter?.startListening()
        binding.rvPharmaciesToCall.adapter = pharmaciesToCallAdapter

    }

    private fun launchIHaveCalledDialog(model: PharmacyData) {

        val builder = layoutInflater.inflate(R.layout.i_have_called_layout_dialog, null)

        val tilCallDate =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_called_date)
        val etCallDate = builder.findViewById<TextInputEditText>(R.id.i_have_called_date)
        val tilCallTime =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_called_time)
        val etCallTime = builder.findViewById<TextInputEditText>(R.id.i_have_called_time)
        val tilCallResponse =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_called_response)
        val autoCompeteCallResponse =
            builder.findViewById<AutoCompleteTextView>(R.id.auto_complete_i_have_called_response)
        val tilCallComment =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_called_comment)
        val etCallComment = builder.findViewById<TextInputEditText>(R.id.i_have_called_comment)
        val btnIHaveCalled = builder.findViewById<Button>(R.id.i_have_called_submit_button)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(builder)
            .setCancelable(false)
            .create()


        dialog.show()



        etCallDate!!.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showDatePicker(view)
            }

            etCallTime!!.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showTimePicker(view)
                }


                val callResponseArray = resources.getStringArray(R.array.call_response_type)
                val callResponseArrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.drop_down_item, callResponseArray)
                autoCompeteCallResponse.setAdapter(callResponseArrayAdapter)

                btnIHaveCalled.setOnClickListener {
                    val dateCalled = etCallDate.text.toString().trim()
                    val timeCalled = etCallTime.text.toString().trim()
                    val callResponse = autoCompeteCallResponse.text.toString().trim()
                    val callComment = etCallComment.text.toString().trim()

                    if (dateCalled.isEmpty()) {
                        tilCallDate.error =
                            resources.getString(R.string.i_have_called_date_empty_error)
                    }
                    if (timeCalled.isEmpty()) {
                        tilCallTime.error =
                            resources.getString(R.string.i_have_called_time_empty_error)
                    }
                    if (callResponse.isEmpty()) {
                        tilCallResponse.error =
                            resources.getString(R.string.i_have_called_response_empty_error)
                    }
                    if (callComment.isEmpty()) {
                        tilCallComment.error =
                            resources.getString(R.string.i_have_called_comment_empty_error)
                    } else {
                        updatePharmacyCallStatus(
                            model,
                            dateCalled,
                            timeCalled,
                            callResponse,
                            callComment
                        )
                        dialog.dismiss()
                    }
                }

            }
        }
    }

    private fun updatePharmacyCallStatus(
        model: PharmacyData,
        dateCalled: String,
        timeCalled: String,
        callResponse: String,
        callComment: String
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val pharmacyRef = Common.pharmaciesCollectionRef.document(model.uid)

            val updateCallStatus = hashMapOf<String, Any>(
                "hasCalled" to true,
                "callerId" to loggedInMarketer.name,
                "callResponse" to callResponse,
                "callComment" to callComment,
                "dateCalled" to dateCalled,
                "timeCalled" to timeCalled
            )

            pharmacyRef.update(updateCallStatus)
                .addOnSuccessListener {
                    requireView().snackBar(resources.getString(R.string.call_status_updated))
                    getPharmaciesToCall()

                }
                .addOnFailureListener { e ->
                    requireContext().toast(e.message.toString().trim())

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

    private fun showDatePicker(view: View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Update the selected date in the calendar instance
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Perform any desired action with the selected date
                // For example, update a TextView with the selected date
                val formattedDate =
                    SimpleDateFormat("EEEE, dd MMMM, yyyy", Locale.getDefault()).format(calendar.time)
                val meetingScheduleDate = view as TextInputEditText
                meetingScheduleDate.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker(view: View) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                // Update the selected time in the calendar instance
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                // Perform any desired action with the selected time
                // For example, update a TextView with the selected time
                val formattedTime =
                    SimpleDateFormat("HH:mm a", Locale.getDefault()).format(calendar.time)
                val bookAppointmentTime = view as TextInputEditText
                bookAppointmentTime.setText(formattedTime)
            }, hour, minute, false)

        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}