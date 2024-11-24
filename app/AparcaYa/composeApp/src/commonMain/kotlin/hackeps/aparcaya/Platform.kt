package hackeps.aparcaya

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform