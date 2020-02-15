package ru.zeburek.onwarranty.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.zeburek.onwarranty.BROADCAST_SIGN_IN_FINISHED
import ru.zeburek.onwarranty.BROADCAST_SIGN_IN_STARTED
import ru.zeburek.onwarranty.MainActivity
import ru.zeburek.onwarranty.R


const val TAG = "SETTINGS"


class SettingsFragment : Fragment(), View.OnClickListener {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var root: View
    private lateinit var _context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsViewModel.text.observe(this, Observer {
            textSettings.text = it
        })
        init()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        glSignIn.setSize(SignInButton.SIZE_WIDE)
        glSignIn.setOnClickListener(this)
        glSignOut.setOnClickListener(this)
        updateSettings()
    }

    fun init() {
        initReceivers()
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseAuth.getInstance().addAuthStateListener { updateSettings() }
    }

    fun initReceivers() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BROADCAST_SIGN_IN_FINISHED -> updateSettings()
                    BROADCAST_SIGN_IN_STARTED -> progressBar.visibility = View.VISIBLE
                }
            }
        }
        _context.registerReceiver(
            broadcastReceiver, IntentFilter(
                BROADCAST_SIGN_IN_FINISHED
            )
        )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _context.unregisterReceiver(broadcastReceiver)
    }

    private fun updateSettings() {
        Log.i(TAG, "Updating view")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            settingsViewModel.text.observe(this, Observer {
                textSettings.text = firebaseUser.displayName
            })
            glSignIn.visibility = View.GONE
            glSignOut.visibility = View.VISIBLE
        } else {
            settingsViewModel.text.observe(this, Observer {
                textSettings.text = it
            })
            glSignIn.visibility = View.VISIBLE
            glSignOut.visibility = View.GONE
        }
        progressBar.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.glSignIn -> (activity as MainActivity).signIn()
            R.id.glSignOut -> FirebaseAuth.getInstance().signOut()
        }
    }
}