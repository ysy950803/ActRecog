package com.ysy.actrecog

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "TEST-1"
        const val TRANSITIONS_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION"

        const val IN_ROAD_VEHICLE = 16
        const val IN_RAIL_VEHICLE = 17
        const val IN_TWO_WHEELER_VEHICLE = 18
        const val IN_FOUR_WHEELER_VEHICLE = 19
    }

    private lateinit var mLogFragment: LogFragment
    private lateinit var mPendingIntent: PendingIntent
    private var mTransitionsReceiver: TransitionsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPendingIntent = PendingIntent.getBroadcast(this, 0,
                Intent(TRANSITIONS_RECEIVER_ACTION), PendingIntent.FLAG_UPDATE_CURRENT)
        mLogFragment = supportFragmentManager.findFragmentById(R.id.log_fragment) as LogFragment
        mTransitionsReceiver = TransitionsReceiver()
        registerReceiver(mTransitionsReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
    }

    override fun onResume() {
        super.onResume()
        setupActivityTransitions()
    }

    override fun onPause() {
        super.onPause()
//        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mPendingIntent)
//                .addOnSuccessListener {
//                    Log.d(TAG, "Transitions successfully unregistered.")
//                }
//                .addOnFailureListener {
//                    Log.e(TAG, "Transitions could not be unregistered: $it")
//                }
        ActivityRecognitionClient(this).removeActivityUpdates(mPendingIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTransitionsReceiver != null) {
            unregisterReceiver(mTransitionsReceiver)
            mTransitionsReceiver = null
        }
    }

    private fun setupActivityTransitions() {
        val transitions = ArrayList<ActivityTransition>()
        addTransition(transitions, DetectedActivity.WALKING, true)
        addTransition(transitions, DetectedActivity.WALKING, false)
        addTransition(transitions, DetectedActivity.STILL, true)
        addTransition(transitions, DetectedActivity.STILL, false)
        val request = ActivityTransitionRequest(transitions)
//        ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, mPendingIntent)
//                .addOnSuccessListener {
//                    Log.d(TAG, "Transitions Api was successfully registered.")
//                }
//                .addOnFailureListener {
//                    Log.d(TAG, "Transitions Api could not be registered: $it")
//                }

        ActivityRecognitionClient(this).requestActivityUpdates(0, mPendingIntent)
    }

    private fun addTransition(list: ArrayList<ActivityTransition>, type: Int, enterOrExit: Boolean) {
        list.add(ActivityTransition.Builder()
                .setActivityType(type)
                .setActivityTransition(
                        if (enterOrExit) ActivityTransition.ACTIVITY_TRANSITION_ENTER
                        else ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
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

    inner class TransitionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (!TextUtils.equals(TRANSITIONS_RECEIVER_ACTION, intent!!.action)) {
                mLogFragment.logView!!
                        .println("Received an unsupported action in TransitionsReceiver: action="
                                + intent.action!!)
                return
            }
//            if (ActivityTransitionResult.hasResult(intent)) {
//                val result = ActivityTransitionResult.extractResult(intent)
//                for (event in result!!.transitionEvents) {
//                    val actType = toActivityString(event.activityType)
//                    val transType = toTransitionType(event.transitionType)
//                    mLogFragment!!.logView!!
//                            .println("Transition: "
//                                    + actType + " (" + transType + ")" + "   "
//                                    + SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                                    .format(Date()))
//                }
//            }
            if (ActivityRecognitionResult.hasResult(intent)) {
                val result = ActivityRecognitionResult.extractResult(intent)
                mLogFragment.logView!!
                        .println(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                + "\n" + result.probableActivities.map {
                            val typeDes = when (it.type) {
                                DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
                                DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
                                DetectedActivity.ON_FOOT -> "ON_FOOT"
                                DetectedActivity.STILL -> "STILL"
                                DetectedActivity.UNKNOWN -> "UNKNOWN"
                                DetectedActivity.TILTING -> "TILTING"
                                DetectedActivity.WALKING -> "WALKING"
                                DetectedActivity.RUNNING -> "RUNNING"
                                IN_ROAD_VEHICLE -> "IN_ROAD_VEHICLE"
                                IN_RAIL_VEHICLE -> "IN_RAIL_VEHICLE"
                                IN_TWO_WHEELER_VEHICLE -> "IN_TWO_WHEELER_VEHICLE"
                                IN_FOUR_WHEELER_VEHICLE -> "IN_FOUR_WHEELER_VEHICLE"
                                else -> "OTHERS: " + it.type
                            }
                            "$typeDes confidence: ${it.confidence}"
                        }.reduceRight { a, b -> "$a\n$b" })
            }
        }
    }
}
