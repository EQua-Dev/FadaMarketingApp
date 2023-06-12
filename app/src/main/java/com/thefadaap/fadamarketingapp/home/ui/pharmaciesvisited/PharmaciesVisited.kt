@file:Suppress("DEPRECATION")

package com.thefadaap.fadamarketingapp.home.ui.pharmaciesvisited

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.databinding.FragmentPharmaciesVisitedBinding
import com.thefadaap.fadamarketingapp.home.data.PharmacyData
import com.thefadaap.fadamarketingapp.home.ui.HomeLandingDirections
import com.thefadaap.fadamarketingapp.utils.Common
import com.thefadaap.fadamarketingapp.utils.toast

class PharmaciesVisited : Fragment() {

    private var _binding: FragmentPharmaciesVisitedBinding? = null
    private val binding get() = _binding!!

    private var pharmaciesVisitedAdapter: FirestoreRecyclerAdapter<PharmacyData, PharmaciesVisitedAdapter>? =
        null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPharmaciesVisitedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPharmaciesVisited()
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext())
            rvPharmaciesVisited.layoutManager = layoutManager
            rvPharmaciesVisited.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )
        }
    }

    private fun getPharmaciesVisited() {

        val pharmaciesVisited =
            Common.pharmaciesCollectionRef.whereEqualTo("hasVisited", true)
                .whereEqualTo("hasCalled", true).whereEqualTo("isDeleted", false)
        val options = FirestoreRecyclerOptions.Builder<PharmacyData>()
            .setQuery(pharmaciesVisited, PharmacyData::class.java).build()
        try {
            pharmaciesVisitedAdapter = object :
                FirestoreRecyclerAdapter<PharmacyData, PharmaciesVisitedAdapter>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PharmaciesVisitedAdapter {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.pharmacies_visited_item_layout, parent, false)
                    return PharmaciesVisitedAdapter(itemView)
                }

                override fun onBindViewHolder(
                    holder: PharmaciesVisitedAdapter,
                    position: Int,
                    model: PharmacyData
                ) {


                    holder.pharmacyDateVisited.text =
                        model.dateVisited
                    holder.pharmacyName.text = model.name
                    holder.pharmacyRoute.text = model.route
                    holder.pharmacyType.text = model.type
                    when (model.visitResponse){
                        resources.getString(R.string.call_response_positive) ->{
                            holder.pharmacyVisitResponse.setTextColor(resources.getColor(R.color.primary))
                            holder.pharmacyVisitResponse.text = model.visitResponse
                        }
                        resources.getString(R.string.call_response_neutral) ->{
                            holder.pharmacyVisitResponse.setTextColor(resources.getColor(R.color.secondary))
                            holder.pharmacyVisitResponse.text = model.visitResponse
                        }
                        resources.getString(R.string.call_response_negative) ->{
                            holder.pharmacyVisitResponse.setTextColor(resources.getColor(R.color.negative))
                            holder.pharmacyVisitResponse.text = model.visitResponse
                        }
                    }


                    holder.itemView.setOnClickListener {
                        val navToPharmacyProfile =
                            HomeLandingDirections.actionHomeLandingToPharmacyProfile(
                                model.uid
                            )
                        findNavController().navigate(navToPharmacyProfile)
                    }

                }

            }

        } catch (e: Exception) {
            requireActivity().toast(e.message.toString())
        }
        pharmaciesVisitedAdapter?.startListening()
        binding.rvPharmaciesVisited.adapter = pharmaciesVisitedAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}