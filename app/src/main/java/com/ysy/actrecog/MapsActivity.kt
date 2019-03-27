package com.ysy.actrecog

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*

class MapsActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "TEST-1"
        const val GEO_ID = "geo_id"
    }

    //    private lateinit var mMap: GoogleMap
    private lateinit var mPendingIntent: PendingIntent
    private var mTransitionsReceiver: TransitionsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        findViewById<TextView>(R.id.exit_geo_btn).setOnClickListener {

        }

        mPendingIntent = PendingIntent.getBroadcast(this, 0,
                Intent(GEO_ID), PendingIntent.FLAG_UPDATE_CURRENT)
        mTransitionsReceiver = TransitionsReceiver()
        registerReceiver(mTransitionsReceiver, IntentFilter(GEO_ID))
        setupGeofencing()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTransitionsReceiver != null) {
            unregisterReceiver(mTransitionsReceiver)
            mTransitionsReceiver = null
        }
    }

    private fun setupGeofencing() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = Location(LocationManager.NETWORK_PROVIDER).apply {
                latitude = 0.1
                longitude = 0.1
            }
            FusedLocationProviderClient(this).apply {
                setMockMode(true)
                setMockLocation(location)
            }

            GeofencingClient(this)
                    .addGeofences(GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                    .addGeofence(Geofence.Builder()
                            .setCircularRegion(0.1, 0.1, 10.1f)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setLoiteringDelay(3000)
                            .setNotificationResponsiveness(3000)
                            .setRequestId(GEO_ID)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build())
                    .build(), mPendingIntent)
                    .addOnSuccessListener { Log.d(TAG, "addOnSuccessListener") }
                    .addOnFailureListener { Log.d(TAG, "addOnFailureListener: $it") }
        } else {
            Log.d(TAG, "setupGeofencing no perm")
        }
    }

    inner class TransitionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val gI = GeofencingEvent.fromIntent(intent)
            if (gI != null) {
                Log.d(TAG, gI.hasError().toString() + "\n" +
                        gI.errorCode.toString() + "\n" +
                        gI.geofenceTransition.toString() + "\n")
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
}
