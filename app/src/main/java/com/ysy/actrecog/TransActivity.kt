package com.ysy.actrecog

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*

class TransActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "TEST-1"
        const val TRANS_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "_TRANS_RECEIVER_ACTION"
    }

    private lateinit var mLogFragment: LogFragment
    private lateinit var mPendingIntent: PendingIntent
    private var mTransReceiver: TransitionsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans)
        mLogFragment = supportFragmentManager.findFragmentById(R.id.log_fragment) as LogFragment
        mPendingIntent = PendingIntent.getBroadcast(this, 0,
            Intent(TRANS_RECEIVER_ACTION), PendingIntent.FLAG_UPDATE_CURRENT
        )
        registerTrans()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterTrans()
    }

    private fun registerTrans() {
        mTransReceiver = TransitionsReceiver()
        registerReceiver(mTransReceiver, IntentFilter(TRANS_RECEIVER_ACTION))

        val transitions = ArrayList<ActivityTransition>()
        addTransition(transitions, DetectedActivity.WALKING, true)
        addTransition(transitions, DetectedActivity.WALKING, false)
        addTransition(transitions, DetectedActivity.STILL, true)
        addTransition(transitions, DetectedActivity.STILL, false)
        val request = ActivityTransitionRequest(transitions)
        ActivityRecognitionClient(this).requestActivityTransitionUpdates(request, mPendingIntent)
            .addOnSuccessListener { Log.d(TAG, "Transitions Api was successfully registered.") }
            .addOnFailureListener { Log.d(TAG, "Transitions Api could not be registered: $it") }
    }

    private fun unregisterTrans() {
        if (mTransReceiver != null) {
            unregisterReceiver(mTransReceiver)
            mTransReceiver = null
        }
        ActivityRecognitionClient(this).removeActivityTransitionUpdates(mPendingIntent)
            .addOnSuccessListener { Log.d(TAG, "Transitions Api was successfully unregistered.") }
            .addOnFailureListener { Log.d(TAG, "Transitions Api could not be unregistered: $it") }
    }

    inner class TransitionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive")
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent)
                for (event in result!!.transitionEvents) {
                    val actType = toActivityString(event.activityType)
                    val transType = toTransitionType(event.transitionType)
                    mLogFragment.logView!!.println("Transition: $actType ($transType)   "
                        + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
                }
            }
        }
    }

    private fun addTransition(list: ArrayList<ActivityTransition>, type: Int, enterOrExit: Boolean) {
        list.add(ActivityTransition.Builder()
            .setActivityType(type)
            .setActivityTransition(
                if (enterOrExit) ActivityTransition.ACTIVITY_TRANSITION_ENTER
                else ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build()
        )
    }

    private fun toActivityString(actType: Int): String {
        return when (actType) {
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            else -> "UNKNOWN"
        }
    }

    private fun toTransitionType(transType: Int): String {
        return when (transType) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "EXIT"
            else -> "UNKNOWN"
        }
    }
}
