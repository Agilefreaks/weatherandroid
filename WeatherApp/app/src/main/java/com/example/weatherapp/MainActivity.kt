package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.location.*
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {
    private var requestValue: Int=1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title="Weather"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        getLocation()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            requestValue-> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, resources.getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                       // getLocation()
                    }
                } else {
                    Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun requestLocationPermission(){
        val shouldShowRequestPermission=ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermission){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestValue)
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestValue)
            }
        }
    }
    fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
                requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val textView: TextView = findViewById(R.id.coordinatesTextView)
                textView.setText(resources.getString(R.string.location_coordinates,location?.latitude, location?.longitude))
                if(location!=null){
                    val geoCoder=Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    //Code  bellow might work on physical device but throws error on emulator
                    addresses= geoCoder.getFromLocation(location.latitude, location.longitude,1)
                    val result = addresses[0].subAdminArea
                    val locationTextView: TextView = findViewById(R.id.locationTextView)
                    locationTextView.setText(result)
                }
            }
    }

}