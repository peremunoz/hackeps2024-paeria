package hackeps.aparcaya

import android.util.Log
import hackeps.aparcaya.util.ApiResponse

class GetParkingsUseCase {
    private val repository = APIRepository()

    suspend fun invoke() = repository.getParkings()
}

class SubscribeParkingUseCase {
    private val repository = APIRepository()

    suspend fun invoke(parkingId: String, userId: String, notificationId: String) {
        try {
            repository.subscribeParking(parkingId, userId, notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("SubscribeParkingUseCase", "Error subscribing parking + $e")
        }
    }
}

class GetSubscribedParkingsUseCase {
    private val repository = APIRepository()

    suspend fun invoke(userID: String) : List<String> = repository.getParkingSubscribed(userID)
}

class PredictUseCase {
    private val repository = APIRepository()

    suspend fun invoke(startDatetime: String, endDatetime: String) : ApiResponse {
        return repository.predict(startDatetime, endDatetime)
    }
}


