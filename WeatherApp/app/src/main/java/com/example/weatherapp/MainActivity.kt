package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import android.widget.ImageView
import android.widget.TextView
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.example.GetWeatherQuery
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var requestValue: Int = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationName: String = "Timisoara"
    var countryName: String = "RO"
    var flag: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Weather"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
    }

    private fun getWeather() {
        runBlocking {
            withContext(Dispatchers.IO) {
                displayWeather()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            requestValue -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.permission_granted),
                            Toast.LENGTH_SHORT
                        ).show()
                        getLocation()
                    }
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    fun requestLocationPermission() {
        val shouldShowRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestValue
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestValue
                )
            }
        }
        else {
            getLocation()
        }
    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val textView: TextView = findViewById(R.id.coordinatesTextView)
                textView.setText(
                    resources.getString(
                        R.string.location_coordinates,
                        location?.latitude,
                        location?.longitude
                    )
                )
                if (location != null) {
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    locationName = addresses[0].subAdminArea
                    countryName = addresses[0].countryCode
                    getWeather()
                }
            }


    }

    suspend fun displayWeather() {
        val apolloClient =
            ApolloClient.builder().serverUrl("https://graphql-weather-api.herokuapp.com/").build()

        coroutineScope {
            launch {
                val response =
                    try {
                        apolloClient.query(GetWeatherQuery(locationName, countryName)).await()
                    } catch (e: Exception) {
                        throw(e)
                    }
                val weatherData = response.data?.getCityByName?.weather
                val weatherDescription = weatherData?.summary?.description
                val humidity = weatherData?.clouds?.humidity.toString()
                val visibility = weatherData?.clouds?.visibility.toString()
                val temperature = weatherData?.temperature?.actual.toString()
                val url =
                    URL(resources.getString(R.string.iconsURL, weatherData?.summary?.icon))
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                val iconView: ImageView = findViewById(R.id.iconView)
                iconView.setImageBitmap(bmp)
                val temperatureTextView: TextView = findViewById(R.id.temperatureTextView)
                temperatureTextView.setText(resources.getString(R.string.metrics, temperature))
                val cloudsTextView: TextView = findViewById(R.id.cloudsTextView)
                cloudsTextView.setText(
                    resources.getString(
                        R.string.clouds,
                        humidity,
                        visibility
                    )
                )
                val weatherDescriptionTextView: TextView =
                    findViewById(R.id.weatherDescriptionView)
                weatherDescriptionTextView.setText(weatherDescription)
                val locationTextView: TextView = findViewById(R.id.locationNameTextView)
                locationTextView.setText(locationName)

            }

        }

    }

}