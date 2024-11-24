package hackeps.aparcaya.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackeps.aparcaya.GetParkingsUseCase
import hackeps.aparcaya.util.Parking
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private var _parkings : MutableLiveData<List<Parking>> = MutableLiveData()
    val parkings: LiveData<List<Parking>> get() = _parkings

    fun setParkings(parkings: List<Parking>) {
        Log.d("MainViewModel", "Setting parkings: $parkings")
        _parkings.value = parkings
    }

    private var _selectedParking : MutableLiveData<Parking?> = MutableLiveData()
    val selectedParking: LiveData<Parking?> get() = _selectedParking

    private var _showBottomSheet : MutableLiveData<Boolean> = MutableLiveData()
    val showBottomSheet: LiveData<Boolean> get() = _showBottomSheet

    private val _subscribedParkings : MutableLiveData<List<String>> = MutableLiveData()
    val subscribedParkings: LiveData<List<String>> get() = _subscribedParkings

    fun setSubscribedParkings(subscribedParkings: List<String>) {
        Log.d("MainViewModel", "Setting subscribed parkings: $subscribedParkings")
        _subscribedParkings.value = subscribedParkings
    }
    
    fun setShowBottomSheet(show: Boolean) {
        Log.d("MainViewModel", "Setting show bottom sheet: $show")
        _showBottomSheet.value = show
    }

    fun setSelectedParking(parking: Parking?) {
        Log.d("MainViewModel", "Setting selected parking: $parking")
        viewModelScope.launch {
            setParkings(GetParkingsUseCase().invoke().parkings)
            _selectedParking.value = parking
            if (parking != null) {
                setShowBottomSheet(true)
            } else {
                setShowBottomSheet(false)
            }
        }
    }
}