package com.ysy.actrecog

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest

class GeoActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "TEST-1"
        const val GEO_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "_GEO_RECEIVER_ACTION"
    }

    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mDataTextView: TextView
    private var mGeoReceiver: GeoReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo)
        mPendingIntent = PendingIntent.getBroadcast(this, 0,
            Intent(GEO_RECEIVER_ACTION), PendingIntent.FLAG_UPDATE_CURRENT)
        mDataTextView = findViewById(R.id.geo_text)
        registerGeo()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterGeo()
    }

    private fun registerGeo() {
        mGeoReceiver = GeoReceiver()
        registerReceiver(mGeoReceiver, IntentFilter(GEO_RECEIVER_ACTION))

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GeofencingClient(this).addGeofences(GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofence(Geofence.Builder()
                    .setCircularRegion(37.422, -122.084, 0.01f)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(3000)
                    .setNotificationResponsiveness(3000)
                    .setRequestId(BuildConfig.APPLICATION_ID)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build())
                .build(), mPendingIntent)
                .addOnSuccessListener { Log.d(TAG, "addGeofences success") }
                .addOnFailureListener { Log.d(TAG, "addGeofences failure: $it") }
        } else {
            Log.d(TAG, "ACCESS_FINE_LOCATION not granted")
        }
    }

    private fun unregisterGeo() {
        if (mGeoReceiver != null) {
            unregisterReceiver(mGeoReceiver)
            mGeoReceiver = null
        }
        GeofencingClient(this).removeGeofences(mPendingIntent)
    }

    inner class GeoReceiver : BroadcastReceiver() {

        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive" + intent?.action)
            val gI = GeofencingEvent.fromIntent(intent) ?: return
            mDataTextView.text = gI.hasError().toString() + "\n" +
                gI.errorCode.toString() + "\n" +
                gI.geofenceTransition.toString()
        }
    }
}
