package ru.zeburek.onwarranty.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import ru.zeburek.onwarranty.models.WARRANTIES_TBL
import ru.zeburek.onwarranty.models.Warranty

class HomeViewModel : ViewModel() {

    private var firebaseDatabase: FirebaseDatabase
    private var databaseReference: DatabaseReference

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(WARRANTIES_TBL)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _warranties_number.value = dataSnapshot.children.map { elem -> elem.getValue(Warranty::class.java) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(this.javaClass.toString(), "Load Warranties", databaseError.toException())
            }
        })

    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    private val _warranties_number = MutableLiveData<List<Warranty?>>().apply {
        value = ArrayList()
    }

    val warranties_number: LiveData<List<Warranty?>> = _warranties_number
}