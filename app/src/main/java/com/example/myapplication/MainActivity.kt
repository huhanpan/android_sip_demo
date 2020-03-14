package com.example.myapplication

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.sip.SipAudioCall
import android.net.sip.SipManager
import android.net.sip.SipProfile
import android.net.sip.SipRegistrationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val userName = ""
    val passWord = ""
    val domain = ""
    val ACTION = "android.SipDemo.INCOMING_CALL"
    val tag = this.javaClass.name
    val sipManager: SipManager? by lazy(LazyThreadSafetyMode.NONE) {
        SipManager.newInstance(this)
    }

    private var sipProfile: SipProfile? = null

    lateinit var callReceiver: IncomingCallReceiver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filter = IntentFilter().apply {
            addAction(ACTION)
        }
        callReceiver = IncomingCallReceiver()
        this.registerReceiver(callReceiver, filter)

        btn_register.setOnClickListener {
            val builder = SipProfile.Builder(userName, domain).setPort(7060)
                    .setPassword(passWord)
            sipProfile = builder.build()

            val intent = Intent(ACTION)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA)
            sipManager?.open(sipProfile, pendingIntent, object : SipRegistrationListener {

                override fun onRegistering(localProfileUri: String) {
                    Log.i(tag,"Registering with SIP Server...")
                }

                override fun onRegistrationDone(localProfileUri: String, expiryTime: Long) {
                    Log.i(tag,"Ready")
                }

                override fun onRegistrationFailed(
                        localProfileUri: String,
                        errorCode: Int,
                        errorMessage: String
                ) {
                    Log.i(tag,"Registration failed. Please check settings.")
                }
            })
        }
        btn_call.setOnClickListener {
            call()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            sipManager?.close(sipProfile?.uriString)
        } catch (ee: Exception) {
            Log.d(tag, "Failed to close local profile.", ee)
        }
    }

    fun call(){
        val listener: SipAudioCall.Listener = object : SipAudioCall.Listener() {

            override fun onCallEstablished(call: SipAudioCall) {
                call.apply {
                    startAudio()
                    setSpeakerMode(true)
                    toggleMute()
                }
            }

            override fun onCallEnded(call: SipAudioCall) {
                // Do something.
                Log.i(tag,"call end")
            }

            override fun onCalling(call: SipAudioCall?) {
                Log.i(tag,"onCalling")
            }

            override fun onRinging(call: SipAudioCall?, caller: SipProfile?) {
                Log.i(tag,"onRinging")
            }

            override fun onReadyToCall(call: SipAudioCall?) {
                Log.i(tag,"onReadyToCall")
            }

            override fun onRingingBack(call: SipAudioCall?) {
                Log.i(tag,"onRingingBack")
            }

            override fun onChanged(call: SipAudioCall?) {
                Log.i(tag,"onChanged")
            }
        }

        val call: SipAudioCall? = sipManager?.makeAudioCall(
                sipProfile?.uriString,
                "",
                listener,
                30
        )
    }
}
