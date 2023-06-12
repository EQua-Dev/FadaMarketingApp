package com.thefadaap.fadamarketingapp.home.ui.pharmaciestocall

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thefadaap.fadamarketingapp.R

class PharmaciesToCallAdapter(itemView: View): RecyclerView.ViewHolder(itemView) {

    var pharmacyDateAdded: TextView
    var pharmacyName: TextView
    var pharmacyType: TextView
    var pharmacyRoute: TextView
    var pharmacyIHaveCalled: Button



    init {
        pharmacyDateAdded = itemView.findViewById(R.id.pharmacy_to_call_item_date_added)
        pharmacyName = itemView.findViewById(R.id.pharmacy_to_call_item_pharmacy_name)
        pharmacyType = itemView.findViewById(R.id.pharmacy_to_call_item_pharmacy_type)
        pharmacyRoute = itemView.findViewById(R.id.pharmacy_to_call_item_pharmacy_route)
        pharmacyIHaveCalled = itemView.findViewById(R.id.pharmacy_to_call_item_i_have_called_button)

    }
}