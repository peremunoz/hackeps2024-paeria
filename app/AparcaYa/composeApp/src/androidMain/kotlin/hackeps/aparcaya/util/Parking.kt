package hackeps.aparcaya.util

data class ParkingsList (
    val parkings: List<Parking>
)

data class SubscribedList (
    val parking_ids: List<String>
)

data class Parking (
    val total_capacity: Int,
    val latitude: String,
    val gate_mode: String,
    val longitude: String,
    val id: String,
    val name: String,
    val occupied_places: Int,
)

data class SubscribeParking (
    val user_id: String,
    val parking_id: String,
    val notification_id: String
)

data class TimeData(
    val type: String,
    val occupacy: Double
)

data class ApiResponse(
    val msg: String,
    val data: Map<String, TimeData>
)

data class PredictRequest(
    val start_datetime: String,
    val end_datetime: String
)