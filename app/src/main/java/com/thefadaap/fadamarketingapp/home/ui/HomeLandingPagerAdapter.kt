/*
 * Copyright (c) 2023.
 * Richard Uzor
 * For Afro Connect Technologies
 * Under the Authority of Devstrike Digital Ltd.
 *
 */

@file:Suppress("DEPRECATION")

package com.thefadaap.fadamarketingapp.home.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thefadaap.fadamarketingapp.home.ui.pharmaciestocall.PharmaciesToCall
import com.thefadaap.fadamarketingapp.home.ui.pharmaciestovisit.PharmaciesToVisit
import com.thefadaap.fadamarketingapp.home.ui.pharmaciesvisited.PharmaciesVisited

@Suppress("DEPRECATION")
class HomeLandingPagerAdapter (var context: FragmentActivity?,
                               fm: FragmentManager,
                               private var totalTabs: Int
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return totalTabs
    }

    //when each tab is selected, define the fragment to be implemented
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PharmaciesToCall()
            }
            1 -> {
                PharmaciesToVisit()
            }
            2 -> {
                PharmaciesVisited()
            }
            else -> getItem(position)
        }
    }
}