rootProject.name = "AparcaYa"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic") {
                    credentials {
                        username = "mapbox"
                        password = "sk.eyJ1IjoicGVyZW11bm96IiwiYSI6ImNtM3U5M3d1czBoZm4ya3Iwem93eHM1czIifQ.b87u9GvFH1yJOhXO5cF1oA"
                    }
                }
            }
        }
    }
}

include(":composeApp")