@file:Suppress("DEPRECATION")

package com.thefadaap.fadamarketingapp.home.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.thefadaap.fadamarketingapp.R
import com.thefadaap.fadamarketingapp.databinding.FragmentHomeLandingBinding

class HomeLanding : Fragment() {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            //set the title to be displayed on each tab

            val tabTitles = resources.getStringArray(R.array.tab_titles)
            for (title in tabTitles){
                val tab = homeLandingTabLayout.newTab()
                tab.text = title
                homeLandingTabLayout.addTab(tab)
            }
//            homeLandingTabLayout.addTab(homeLandingTabLayout.newTab().text(resources.getString(R.string.pharmacies_to_call_tab_title)"Upcoming"))
//            homeLandingTabLayout.addTab(homeLandingTabLayout.newTab().setText("Concluded"))

            homeLandingTabLayout.tabGravity = TabLayout.GRAVITY_FILL

            val adapter = HomeLandingPagerAdapter(
                activity,
                childFragmentManager,
                homeLandingTabLayout.tabCount
            )
            homeLandingLandingViewPager.adapter = adapter
            homeLandingLandingViewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    homeLandingTabLayout
                )
            )

            //define the functionality of the tab layout
            homeLandingTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    homeLandingLandingViewPager.currentItem = tab.position
                    homeLandingTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.secondary))
                    homeLandingTabLayout.setTabTextColors(
                        Color.BLACK,
                        resources.getColor(R.color.secondary)
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    homeLandingTabLayout.setTabTextColors(Color.WHITE, Color.BLACK)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            fabAddPharmacy.setOnClickListener {
                val navToAddPharmacy = HomeLandingDirections.actionHomeLandingToAddPharmacy()
                findNavController().navigate(navToAddPharmacy)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}