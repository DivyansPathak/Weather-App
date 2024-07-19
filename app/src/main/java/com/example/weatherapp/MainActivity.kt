package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.search.SearchBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import weatherApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//  10e29d802191c0ebd7ce668ac55d6cd4

// a06c0689fda3d863031e0ab068cfe54d

//


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fetchWeatherData("new delhi")
        searchCity()

        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.exit_alert))
                    .setMessage(getString(R.string.do_you_want_to_exit))
                    .setPositiveButton("Yes"){_,_->
                        finishAffinity()
                    }
                    .setNegativeButton("No"){_,_->

                    }
                    .setCancelable(true)
                    .create()
                    .show()

            }
        })




    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchWeatherData(city : String) {
        val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Please Wait")
            progressDialog.show()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(city,"a06c0689fda3d863031e0ab068cfe54d","metric")
        response.enqueue(object : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                progressDialog.dismiss()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val humidity = responseBody.main.humidity.toString()


//                    Log.e("TAG","${temperature}")


                    val sunset = responseBody.sys.sunset.toLong()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sealevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"





                    binding.textView5.text = "${temperature}°C"
                    binding.txtMax.text = "${maxTemp}°C"
                    binding.txtMin.text = "${minTemp}°C"
                    binding.txtHumidity.text = "${humidity}%"
                    binding.txtSunSet.text = "${timeName(sunset)}"
                    binding.txtSunRise.text = "${timeName(sunRise)}"
                    binding.txtWind.text = "${windSpeed}m/s"
                    binding.txtSea.text = "${sealevel}hPa"
                    binding.txtCondition.text = "${condition}"
                    binding.textView6.text = "${condition}"
                    binding.textViewCity.text = "${city}"
                    binding.txtDay.text = dayName(System.currentTimeMillis())
                    binding.txtDate.text = dateName()



                    changeCondition(condition)
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity,"${t.localizedMessage}",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })


    }

    private fun changeCondition(condition : String) {
        when(condition){
            "Haze","Clouds","Partly Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            "Thunderstorm","thunderstorm"->{
                binding.root.setBackgroundResource(R.drawable.thunderstrom_background)
                binding.lottieAnimationView.setAnimation(R.raw.thunderstorm)

            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }

        }
    }

    fun dateName():String{
        val sdp = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
        return sdp.format(Date())
    }

    fun dayName(timeStamp : Long) : String{
        val sdp = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdp.format(Date())
    }

    fun timeName(timeStamp: Long):String{
        val sdp = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdp.format(Date(timeStamp*1000))
    }


}