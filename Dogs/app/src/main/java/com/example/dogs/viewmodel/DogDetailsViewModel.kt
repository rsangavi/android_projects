package com.example.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogDatabase
import kotlinx.coroutines.launch

class DogDetailsViewModel(application: Application): BaseViewModel(application) {
    val dogBreedList = MutableLiveData<DogBreed>()

    fun getDetailsData(dogUuid: Int) {
        launch {
            val dogInfo = DogDatabase(getApplication()).DogDao().getDog(dogUuid)
            dogBreedList.value = dogInfo
        }
    }
}