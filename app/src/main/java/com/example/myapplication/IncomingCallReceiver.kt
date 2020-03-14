package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.sip.SipAudioCall
import android.net.sip.SipProfile
import android.util.Log

class IncomingCallReceiver : BroadcastReceiver() {
    private val tag = this.javaClass.name
    override fun onReceive(context: Context, intent: Intent) {
        val wtActivity = context as MainActivity

        var incomingCall: SipAudioCall? = null
        try {
            incomingCall = wtActivity.sipManager?.takeAudioCall(intent, listener)
            incomingCall?.apply {
                answerCall(30)
                startAudio()
                setSpeakerMode(true)
                if (isMuted) {
                    toggleMute()
                }
            }
        } catch (e: Exception) {
            incomingCall?.close()
        }
    }

    private val listener = object : SipAudioCall.Listener() {

        override fun onRinging(call: SipAudioCall, caller: SipProfile) {
            try {
                Log.i(tag,"onRing")
                call.answerCall(30)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onCallEnded(call: SipAudioCall?) {
            super.onCallEnded(call)
            Log.i(tag,"onCallEnded")
        }


    }
}