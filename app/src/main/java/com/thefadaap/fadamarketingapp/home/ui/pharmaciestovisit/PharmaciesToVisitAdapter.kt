package com.thefadaap.fadamarketingapp.home.ui.pharmaciestovisit

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thefadaap.fadamarketingapp.R

class PharmaciesToVisitAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var pharmacyDateCalled: TextView
    var pharmacyName: TextView
    var pharmacyType: TextView
    var pharmacyRoute: TextView
    var pharmacyCallResponse: TextView
    var pharmacyIHaveVisited: Button


    init {
        pharmacyDateCalled = itemView.findViewById(R.id.pharmacy_to_visit_item_date_called)
        pharmacyName = itemView.findViewById(R.id.pharmacy_to_visit_item_pharmacy_name)
        pharmacyType = itemView.findViewById(R.id.pharmacy_to_visit_item_pharmacy_type)
        pharmacyRoute = itemView.findViewById(R.id.pharmacy_to_visit_item_pharmacy_route)
        pharmacyCallResponse =
            itemView.findViewById(R.id.pharmacy_to_visit_item_pharmacy_call_response)
        pharmacyIHaveVisited =
            itemView.findViewById(R.id.pharmacy_to_visit_item_i_have_visited_button)

    }
}