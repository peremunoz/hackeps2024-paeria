package hackeps.aparcaya

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hackeps.aparcaya.core.MainViewModel

@Composable
fun AparcaYaNavHost(
    context: MainActivity,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        startDestination = NavigationRoutes.HOME,
        navController = navController as NavHostController
    ) {
        composable(NavigationRoutes.LOGIN) {
            LoginScreen(context, mainViewModel, navController)
        }

        composable(NavigationRoutes.HOME) {
            HomeScreen(context, mainViewModel, navController)
        }

        composable(NavigationRoutes.SIGNUP) {
            SignupScreen(navController, mainViewModel)
        }
    }
}