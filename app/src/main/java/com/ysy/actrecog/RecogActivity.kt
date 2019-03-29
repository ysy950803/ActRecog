package com.ysy.actrecog

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.text.SimpleDateFormat
import java.util.*

class RecogActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "TEST-1"
        const val RECOG_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "_RECOG_RECEIVER_ACTION"

        const val IN_ROAD_VEHICLE = 16
        const val IN_RAIL_VEHICLE = 17
        const val IN_TWO_WHEELER_VEHICLE = 18
        const val IN_FOUR_WHEELER_VEHICLE = 19
    }

    private lateinit var mLogFragment: LogFragment
    private lateinit var mPendingIntent: PendingIntent
    private var mRecogReceiver: RecogReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recog)
        mLogFragment = supportFragmentManager.findFragmentById(R.id.log_fragment) as LogFragment
        mPendingIntent = PendingIntent.getBroadcast(this, 0,
            Intent(RECOG_RECEIVER_ACTION), PendingIntent.FLAG_UPDATE_CURRENT)
        registerRecog()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterRecog()
    }

    private fun registerRecog() {
        mRecogReceiver = RecogReceiver()
        registerReceiver(mRecogReceiver, IntentFilter(RECOG_RECEIVER_ACTION))
        // 0 -> max speed
        ActivityRecognitionClient(this).requestActivityUpdates(0, mPendingIntent)
            .addOnSuccessListener { Log.d(TAG, "Recognition Api was successfully registered.") }
            .addOnFailureListener { Log.d(TAG, "Recognition Api could not be registered: $it") }
    }

    private fun unregisterRecog() {
        if (mRecogReceiver != null) {
            unregisterReceiver(mRecogReceiver)
            mRecogReceiver = null
        }
        ActivityRecognitionClient(this).removeActivityUpdates(mPendingIntent)
            .addOnSuccessListener { Log.d(TAG, "Recognition Api was successfully unregistered.") }
            .addOnFailureListener { Log.d(TAG, "Recognition Api could not be unregistered: $it") }
    }

    inner class RecogReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive")
            if (ActivityRecognitionResult.hasResult(intent)) {
                val result = ActivityRecognitionResult.extractResult(intent)
                mLogFragment.logView!!.println(
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) + "\n"
                        + result.probableActivities.map {
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
