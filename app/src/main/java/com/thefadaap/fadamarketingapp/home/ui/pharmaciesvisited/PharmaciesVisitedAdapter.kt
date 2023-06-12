package com.thefadaap.fadamarketingapp.home.ui.pharmaciesvisited

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thefadaap.fadamarketingapp.R

class PharmaciesVisitedAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var pharmacyDateVisited: TextView
    var pharmacyName: TextView
    var pharmacyType: TextView
    var pharmacyRoute: TextView
    var pharmacyVisitResponse: TextView


    init {
        pharmacyDateVisited = itemView.findViewById(R.id.pharmacy_visited_item_date_visited)
        pharmacyName = itemView.findViewById(R.id.pharmacy_visited_item_pharmacy_name)
        pharmacyType = itemView.findViewById(R.id.pharmacy_visited_item_pharmacy_type)
        pharmacyRoute = itemView.findViewById(R.id.pharmacy_visited_item_pharmacy_route)
        pharmacyVisitResponse =
            itemView.findViewById(R.id.pharmacy_visited_item_pharmacy_visit_response)

    }
}