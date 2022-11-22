package com.example.dogs.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogDatabase
import com.example.dogs.model.DogsApiService
import com.example.dogs.util.NotificationsHelper
import com.example.dogs.util.SharedPreferencesHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class DogListViewModel(application: Application) : BaseViewModel(application) {

    val dogsLiveData = MutableLiveData<List<DogBreed>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<Boolean>()

    private val dogsDataService = DogsApiService()
    private val disposable = CompositeDisposable()

    private var sharedPreferencesHelper = SharedPreferencesHelper(application)
    private var thresholdTime = 5 * 60 * 1000 * 1000 * 1000L

    fun refresh() {
        checkCacheDuration()
        val updatedTime = sharedPreferencesHelper.getUpdatedTime()
        if(updatedTime != null && updatedTime != 0L &&
                System.nanoTime() - updatedTime  < thresholdTime) {
            fetchDBValues()
        } else {
            fetchRemoteValues()
        }
    }

    private fun checkCacheDuration() {
        try {
            sharedPreferencesHelper.getCacheSettingsTime()?.toInt()?.times(1000 * 1000 * 1000L)
                .also {
                    if (it != null) {
                        thresholdTime = it
                    }
                }
        } catch(e: NumberFormatException){
            e.printStackTrace()
        }

    }

    fun refreshByPassCache() {
        fetchRemoteValues()
    }

    private fun fetchDBValues() {
        loadingLiveData.value = true
        launch {
            val dogList = DogDatabase(getApplication()).DogDao().getAllDogs()
            updateLiveDataValues(dogList)
            Log.d("DogListViewModel", "got data from Database...")
        }
        Toast.makeText(getApplication(), "got data from Database...",
                Toast.LENGTH_SHORT).show()
    }

    private fun fetchRemoteValues() {
        loadingLiveData.value = true
        disposable.add(
                dogsDataService.getDogInfo()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                            override fun onSuccess(dogsList: List<DogBreed>?) {
                                dogsList?.let {
                                    postDogInfoRetrieved(dogsList)
                                    Toast.makeText(getApplication(),
                                            "got data from end point...", Toast.LENGTH_SHORT).show()
                                    Log.d("DogListViewModel", "got data from Endpoint...")
                                    NotificationsHelper(getApplication()).createNotification()
                                }
                            }

                            override fun onError(e: Throwable?) {
                                Log.d("error", "exception: ${e?.message.toString()}")
                                errorLiveData.value = true
                                loadingLiveData.value = false
                            }

                        })

        )
    }

    private fun updateLiveDataValues(dogsList: List<DogBreed>) {
        dogsLiveData.value = dogsList
        loadingLiveData.value = false
    }

    private fun postDogInfoRetrieved(dogsList: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).DogDao()
            dao.deleteAll()
            val result = dao.insertDogsList(*dogsList.toTypedArray())
            var i = 0
            while(i < result.size){
                dogsList[i].uuid = result[i].toInt()
                ++i
            }
            updateLiveDataValues(dogsList)
        }
        sharedPreferencesHelper.saveUpdatedTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}