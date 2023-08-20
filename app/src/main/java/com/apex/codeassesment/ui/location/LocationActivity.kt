package com.apex.codeassesment.ui.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apex.codeassesment.R
import com.apex.codeassesment.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


// TODO (Optional Bonus 8 points): Calculate distance between 2 coordinates using phone's location
class LocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var currentLocation: Location? = null
    var userLocation: Location? = null
    lateinit var binding: ActivityLocationBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                calculateDistance()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                calculateDistance()
            }

            else -> {
                // If user don't allow location permissions.
                Toast.makeText(
                    this,
                    "Please give your location permissions so that you can calculate distance properly!",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val latitudeRandomUser = intent.getStringExtra("user-latitude-key")
        val longitudeRandomUser = intent.getStringExtra("user-longitude-key")
        if (latitudeRandomUser != null && longitudeRandomUser != null) {
            userLocation = Location("user location")
            userLocation!!.latitude = latitudeRandomUser.toDouble()
            userLocation!!.longitude = longitudeRandomUser.toDouble()
        }

        binding.locationRandomUser.text =
            getString(R.string.location_random_user, latitudeRandomUser, longitudeRandomUser)
        binding.locationCalculateButton.setOnClickListener {
            //function to calculate the distance between user location and device location
            //after getting device location permission if not granted before
            calculateDistance()
//            Toast.makeText(
//                this,
//                "TODO (8): Bonus - Calculate distance between 2 coordinates using phone's location",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun calculateDistance() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //check location permissions before getting current location from GPS
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    currentLocation = task.result
                    if (currentLocation != null && userLocation != null) {
                        binding.locationPhone.text = getString(R.string.location_phone, currentLocation!!.latitude.toString(), currentLocation!!.longitude.toString())

                        //you can use this Location's distanceTo function as well as the function you provided
                        // in distance helper class to calculate distance but there is difference of 5 miles
                        // between both of these functions
                        val distanceHelper = DistanceHelper()
                        val distance = //distanceHelper.distance(currentLocation!!.latitude, currentLocation!!.longitude, userLocation!!.latitude, userLocation!!.longitude, 'M')
                            currentLocation!!.distanceTo(
                                userLocation!!
                            ).toDouble() * 0.000621
                        binding.locationDistance.text = getString(R.string.location_result_miles, distance)

                    }
                }
            }
        }
    }
}
