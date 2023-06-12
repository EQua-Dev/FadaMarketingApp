@file:Suppress("NAME_SHADOWING", "DEPRECATION")

package com.thefadaap.fadamarketingapp.home.ui.pharmaciestovisit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.databinding.FragmentPharmaciesToVisitBinding
import com.thefadaap.fadamarketingapp.home.data.PharmacyData
import com.thefadaap.fadamarketingapp.home.ui.HomeLandingDirections
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.snackBar
import com.thefadaap.fadamarketingapp.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PharmaciesToVisit : Fragment() {


    private var _binding: FragmentPharmaciesToVisitBinding? = null
    private val binding get() = _binding!!

    private var pharmaciesToVisitAdapter: FirestoreRecyclerAdapter<PharmacyData, PharmaciesToVisitAdapter>? =
        null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPharmaciesToVisitBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPharmaciesToVisit()
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            rvPharmaciesToVisit.layoutManager = layoutManager
            rvPharmaciesToVisit.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )
        }
    }


    private fun getPharmaciesToVisit() {

        val pharmaciesToVisit =
            Common.pharmaciesCollectionRef.whereEqualTo("hasVisited", false)
                .whereEqualTo("hasCalled", true).whereEqualTo("isDeleted", false)
        val options = FirestoreRecyclerOptions.Builder<PharmacyData>()
            .setQuery(pharmaciesToVisit, PharmacyData::class.java).build()
        try {
            pharmaciesToVisitAdapter = object :
                FirestoreRecyclerAdapter<PharmacyData, PharmaciesToVisitAdapter>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PharmaciesToVisitAdapter {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.pharmacies_to_visit_item_layout, parent, false)
                    return PharmaciesToVisitAdapter(itemView)
                }

                override fun onBindViewHolder(
                    holder: PharmaciesToVisitAdapter,
                    position: Int,
                    model: PharmacyData
                ) {


                    holder.pharmacyDateCalled.text =
                        model.dateCalled
                    holder.pharmacyName.text = model.name
                    holder.pharmacyRoute.text = model.route
                    holder.pharmacyType.text = model.type
                    when (model.callResponse){
                        resources.getString(R.string.call_response_positive) ->{
                            holder.pharmacyCallResponse.setTextColor(resources.getColor(R.color.primary))
                            holder.pharmacyCallResponse.text = model.callResponse
                        }
                        resources.getString(R.string.call_response_neutral) ->{
                            holder.pharmacyCallResponse.setTextColor(resources.getColor(R.color.secondary))
                            holder.pharmacyCallResponse.text = model.callResponse
                        }
                        resources.getString(R.string.call_response_negative) ->{
                            holder.pharmacyCallResponse.setTextColor(resources.getColor(R.color.negative))
                            holder.pharmacyCallResponse.text = model.callResponse
                        }
                    }


                    holder.pharmacyIHaveVisited.setOnClickListener {
                        launchIHaveVisitedDialog(model)
                    }

                    holder.itemView.setOnClickListener {
                        val navToPharmacyProfile =
                            HomeLandingDirections.actionHomeLandingToPharmacyProfile(model.uid)
                        findNavController().navigate(navToPharmacyProfile)
                    }

                }

            }

        } catch (e: Exception) {
            requireActivity().toast(e.message.toString())
        }
        pharmaciesToVisitAdapter?.startListening()
        binding.rvPharmaciesToVisit.adapter = pharmaciesToVisitAdapter

    }

    private fun launchIHaveVisitedDialog(model: PharmacyData) {

        val builder = layoutInflater.inflate(R.layout.i_have_visited_layout_dialog, null)

        val tilVisitDate =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_visited_date)
        val etVisitDate = builder.findViewById<TextInputEditText>(R.id.i_have_visited_date)
        val tilVisitTime =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_visited_time)
        val etVisitTime = builder.findViewById<TextInputEditText>(R.id.i_have_visited_time)
        val tilVisitResponse =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_visited_response)
        val autoCompeteVisitResponse =
            builder.findViewById<AutoCompleteTextView>(R.id.auto_complete_i_have_visited_response)
        val tilVisitComment =
            builder.findViewById<TextInputLayout>(R.id.text_input_layout_i_have_visited_comment)
        val etVisitComment = builder.findViewById<TextInputEditText>(R.id.i_have_visited_comment)
        val btnIHaveVisited = builder.findViewById<Button>(R.id.i_have_visited_submit_button)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(builder)
            .setCancelable(false)
            .create()


        dialog.show()



        etVisitDate!!.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showDatePicker(view)
            }

            etVisitTime!!.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showTimePicker(view)
                }


                val visitResponseArray = resources.getStringArray(R.array.call_response_type)
                val visitResponseArrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.drop_down_item, visitResponseArray)
                autoCompeteVisitResponse.setAdapter(visitResponseArrayAdapter)

                btnIHaveVisited.setOnClickListener {
                    val dateVisited = etVisitDate.text.toString().trim()
                    val timeVisited = etVisitTime.text.toString().trim()
                    val visitResponse = autoCompeteVisitResponse.text.toString().trim()
                    val visitComment = etVisitComment.text.toString().trim()

                    if (dateVisited.isEmpty()) {
                        tilVisitDate.error =
                            resources.getString(R.string.i_have_visited_date_empty_error)
                    }
                    if (timeVisited.isEmpty()) {
                        tilVisitTime.error =
                            resources.getString(R.string.i_have_visited_time_empty_error)
                    }
                    if (visitResponse.isEmpty()) {
                        tilVisitResponse.error =
                            resources.getString(R.string.i_have_visited_response_empty_error)
                    }
                    if (visitComment.isEmpty()) {
                        tilVisitComment.error =
                            resources.getString(R.string.i_have_visited_comment_empty_error)
                    } else {
                        updatePharmacyVisitStatus(
                            model,
                            dateVisited,
                            timeVisited,
                            visitResponse,
                            visitComment
                        )
                        dialog.dismiss()
                    }
                }

            }
        }
    }

    private fun updatePharmacyVisitStatus(
        model: PharmacyData,
        dateVisited: String,
        timeVisited: String,
        visitResponse: String,
        visitComment: String
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val pharmacyRef = Common.pharmaciesCollectionRef.document(model.uid)

            val updateVisitStatus = hashMapOf<String, Any>(
                "hasVisited" to true,
                "visitResponse" to visitResponse,
                "visitComment" to visitComment,
                "dateVisited" to dateVisited,
                "timeVisited" to timeVisited
            )

            pharmacyRef.update(updateVisitStatus)
                .addOnSuccessListener {
                    requireView().snackBar(resources.getString(R.string.visit_status_updated))
                    getPharmaciesToVisit()

                }
                .addOnFailureListener { e ->
                    requireContext().toast(e.message.toString().trim())

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
                    SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(calendar.time)
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