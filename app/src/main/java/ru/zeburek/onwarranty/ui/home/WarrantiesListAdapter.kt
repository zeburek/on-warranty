package ru.zeburek.onwarranty.ui.home

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.zeburek.onwarranty.App
import ru.zeburek.onwarranty.R
import ru.zeburek.onwarranty.models.Warranty
import java.text.ParseException
import java.text.SimpleDateFormat


class WarrantiesListAdapter : RecyclerView.Adapter<WarrantiesListAdapter.WarrantiesViewHolder>() {
    private val warranties: ArrayList<Warranty?> = ArrayList()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class WarrantiesViewHolder(
        val warrantyInfo: ConstraintLayout
    ) : RecyclerView.ViewHolder(warrantyInfo) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val warrantyName: TextView = warrantyInfo.findViewById(R.id.warrantyName)
        val warrantyTimeLeft: TextView = warrantyInfo.findViewById(R.id.warrantyTimeLeft)

        fun bind(warranty: Warranty) {
            val now  = System.currentTimeMillis()
            warrantyName.text = if (warranty.name == "") "-" else warranty.name
            var timeLeft = "-"
            if (warranty.startedAt != null && warranty.endingAt != null) {
                try {
                    val startedAt = dateFormat.parse(warranty.startedAt)
                    val endingAt = dateFormat.parse(warranty.endingAt)
                    if (startedAt != null && endingAt != null)
                        timeLeft = if (now > endingAt.time) {
                            App.appResources!!.getString(R.string.warranty_gone)
                        } else {
                            DateUtils.getRelativeTimeSpanString(
                                endingAt.time,
                                startedAt.time,
                                DateUtils.DAY_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_RELATIVE
                            ).toString()
                        }
                } catch (e: ParseException) {
                }
            }
            warrantyTimeLeft.text = timeLeft

        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WarrantiesViewHolder {
        // create a new view
        val warrantyInfo = LayoutInflater.from(parent.context)
            .inflate(R.layout.warranty_info, parent, false) as ConstraintLayout

        return WarrantiesViewHolder(warrantyInfo)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WarrantiesViewHolder, position: Int) {
        val war = warranties[position]
        if (war != null) holder.bind(war)
    }

    fun setItems(warranties: List<Warranty?>) {
        Log.i("", "Adding new warranties")
        this.warranties.addAll(warranties)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.warranties.clear()
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = warranties.size
}