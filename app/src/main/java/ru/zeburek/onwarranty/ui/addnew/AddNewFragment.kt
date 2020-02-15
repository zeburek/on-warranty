package ru.zeburek.onwarranty.ui.addnew

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_new.*
import ru.zeburek.onwarranty.R
import ru.zeburek.onwarranty.helpers.InputFilterMinMax
import ru.zeburek.onwarranty.models.WARRANTIES_TBL
import ru.zeburek.onwarranty.models.Warranty
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class AddNewFragment : Fragment() {

    private lateinit var addNewViewModel: AddNewViewModel

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(WARRANTIES_TBL)
        addNewViewModel =
            ViewModelProviders.of(this).get(AddNewViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_new, container, false)
        addNewViewModel.text.observe(this, Observer {
            warrantyStartedAt.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val today = Calendar.getInstance()
        warrantyLasts.filters = arrayOf<InputFilter>(InputFilterMinMax(0.0, 100.0))
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        ) { datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            addNewViewModel.text.value = dateFormat.format(cal.time)
        }
        addNewWarrantyBtn.setOnClickListener {
            val name = warrantyProductName.text.toString()
            val startedAt = warrantyStartedAt.text.toString()
            if (startedAt == "") return@setOnClickListener
            val startedAtDate = dateFormat.parse(startedAt)
            val lastsFor = warrantyLasts.text.toString().toDouble()
            val lastsForTime = (lastsFor * 12).roundToInt()
            val cal = Calendar.getInstance()
            if (startedAtDate != null){
                cal.time = startedAtDate
                cal.add(Calendar.MONTH, lastsForTime)
                val key = databaseReference.push().key
                if (key != null){
                    databaseReference.child(key).setValue(Warranty(
                        name = name,
                        startedAt = startedAt,
                        endingAt = dateFormat.format(cal.time)
                    ))
                }
                clearFields()

            }
        }
        clearFields()
    }

    fun clearFields() {
        val cal = Calendar.getInstance()
        warrantyProductName.setText("")
        warrantyStartedAt.setText("")
        warrantyLasts.setText("")
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
    }


}