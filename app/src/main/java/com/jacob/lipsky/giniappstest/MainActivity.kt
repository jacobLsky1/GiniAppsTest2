package com.jacob.lipsky.giniappstest

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.jacob.lipsky.giniappstest.databinding.ActivityMainBinding
import com.jacob.lipsky.giniappstest.services.ApiWorker
import com.jacob.lipsky.giniappstest.services.MainViewModel
import com.jacob.lipsky.giniappstest.util.InternetConnectivity
import com.jacob.lipsky.giniappstest.util.Util
import com.jacob.lipsky.giniappstest.util.WifiReceiver
import com.jacob.lipsky.giniappstest.views.adapters.PhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * wifi reciver to check connection
     */
    private lateinit var wifiReceiver: WifiReceiver

    /**
     * our viewModel
     */
    private val viewModel: MainViewModel by viewModels()

    /**
     * using android databinding
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * for pagination
     */
    var currentVisiblePosition: Int = -1

    /**
     * on the ocation that the app starts without internt the var will be set to false ->
     * when the user connects the app will send a request to the api
     */
    var startSuccess = true

    /**
     * when the app starts the Worker background service will start and keep the information in shared preffs to not start the Worker all over again
     */
    lateinit var sharedPref:SharedPreferences


    /**
     * for the Worker class
     */
    private lateinit var workManager: WorkManager
    private var workRequestId: UUID? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = applicationContext.getSharedPreferences("giniapps", Context.MODE_PRIVATE)
        workManager = WorkManager.getInstance(applicationContext)
        wifiReceiver = WifiReceiver()
        setUpView()
        setUpObservers(this.findViewById(android.R.id.content))
        checkBackgroundWorker()
    }

    /**
     *checks if the Worker class is Active or not and if not starts it
     */
    private fun checkBackgroundWorker(){
        val isWorking = sharedPref.getString("work","")
        if(isWorking!="yes"){
            startPhotoUpdates()
            sharedPref.edit().putString("work","yes").apply()
        }
    }


    fun setUpView(){
        binding.photoRV.layoutManager = LinearLayoutManager(applicationContext)

        /**
         * ScrollListener for pagination and loading more photos
         */
        binding.photoRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    if(!viewModel.isSeraching.value!!) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }


    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiReceiver)
    }


    private fun setUpObservers(view: View){
        val snackBar: Snackbar =
            Snackbar.make(view, "Can't Connect To Web..", Snackbar.LENGTH_INDEFINITE)
                .setAction("GO TO SETTINGS") {
                    this@MainActivity?.let { it1 -> InternetConnectivity.connectToInternet(applicationContext) }
                }
        Util.hasInternet.observe(this) {
            if (!it) {
                snackBar.show()
            } else {
                snackBar.dismiss()
                /**
                 * if there is internet and the was a problem with the init of the app (it started without internet)
                 * then the proccess of getting photos will start again
                 */
                if(viewModel.pageToken==0){
                   viewModel.loadNextPage()
                    startSuccess=true
                }
            }
        }

        Util.requestError.observe(this) {
            if (it != 0) {
                //if (!errorDialogIsShowing)
                    //makeErrorDialog(it)
            }
        }
        viewModel.isSeraching.observe(this){
            binding.progressBar.isVisible = it
        }


        /**
         * observes the photos and displays them in recyclerview
         */
        viewModel.photosRoom.observe(this) { newPhotos ->
            val currentLayoutManager = binding.photoRV.layoutManager
            if (currentLayoutManager != null) {
                currentVisiblePosition = (currentLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
            binding.photoRV.adapter = PhotoAdapter(newPhotos)

            if (currentVisiblePosition != -1) {
                binding.photoRV.scrollToPosition(currentVisiblePosition)
            }
        }

    }

    /**
     * starts the PeriodicWorkRequest that gets photos from the api
     */
    private fun startPhotoUpdates(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ApiWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * delays the Worker GET request to 1400 PM
     */
    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val currentTime = System.currentTimeMillis()
        val initialDelay = if (calendar.timeInMillis <= currentTime) {
            // Add 1 day to the calculated time if it's already passed
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.timeInMillis - currentTime
        } else {
            calendar.timeInMillis - currentTime
        }

        return initialDelay
    }

/*
    private fun makeErrorDialog(num: Int){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.error_request_dalog, null)
        val checkInternetButton = dialogView.findViewById(R.id.checkInternetButton) as Button
        val yesButton = dialogView.findViewById(R.id.tryAgainButton) as Button

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setView(dialogView).setCancelable(true)

        val dialog = alertDialog.create()
        dialog.show()
        errorDialogIsShowing = true

        yesButton.setOnClickListener {
            dialog.dismiss()
            errorDialogIsShowing = false
        }

        checkInternetButton.setOnClickListener {
            this@MainActivity.let { InternetConnectivity.connectToInternet(applicationContext) }
        }
    }

 */

}