package hackeps.aparcaya

import hackeps.aparcaya.util.ApiResponse
import hackeps.aparcaya.util.ParkingsList
import hackeps.aparcaya.util.PredictRequest
import hackeps.aparcaya.util.SubscribeParking
import hackeps.aparcaya.util.SubscribedList
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("parkings/")
    suspend fun getParkings(): ParkingsList

    @POST("users/subscribe_parking")
    suspend fun subscribeParking(@Body body: SubscribeParking): Any

    @GET("users/subscribed_parkings/{user_id}")
    suspend fun getParkingSubscribed(@Path("user_id") userID: String): SubscribedList

    @POST("/predictions/predict")
    suspend fun predict(@Body body: PredictRequest): ApiResponse
}