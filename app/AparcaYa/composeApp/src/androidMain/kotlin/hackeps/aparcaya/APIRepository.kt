package hackeps.aparcaya

import hackeps.aparcaya.util.ApiResponse
import hackeps.aparcaya.util.ParkingsList
import hackeps.aparcaya.util.PredictRequest
import hackeps.aparcaya.util.SubscribeParking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class APIRepository {
    private val api = RetrofitClient.apiService

    suspend fun getParkings(): ParkingsList {
        return withContext(Dispatchers.IO) {
            api.getParkings()
        }
    }

    suspend fun subscribeParking(parkingId: String, userId: String, notificationId: String) {
        return withContext(Dispatchers.IO) {
            api.subscribeParking(SubscribeParking(user_id = userId, parking_id = parkingId, notification_id = notificationId))
        }
    }

    suspend fun getParkingSubscribed(userID: String): List<String> {
        return withContext(Dispatchers.IO) {
            api.getParkingSubscribed(userID).parking_ids
        }
    }

    suspend fun predict(startDatetime: String, endDatetime: String) : ApiResponse {
        return withContext(Dispatchers.IO) {
            api.predict(PredictRequest(start_datetime = startDatetime, end_datetime = endDatetime))
        }
    }
}
