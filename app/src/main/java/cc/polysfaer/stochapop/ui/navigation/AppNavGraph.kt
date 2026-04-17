package cc.polysfaer.stochapop.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cc.polysfaer.stochapop.ui.screens.home.HomeDestination
import cc.polysfaer.stochapop.ui.screens.home.HomeScreen
import cc.polysfaer.stochapop.ui.screens.reminder.EditReminderDestination
import cc.polysfaer.stochapop.ui.screens.reminder.NewReminderDestination
import cc.polysfaer.stochapop.ui.screens.reminder.ReminderEditScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    onTitleResChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navigateBackHome: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(route = HomeDestination.route) {
            LaunchedEffect(Unit) { onTitleResChanged(HomeDestination.titleRes) }
            HomeScreen(
                navigateToNewReminder = {
                    navController.navigate(NewReminderDestination.route)
                },
                navigateToEditReminder = {
                    navController.navigate("${EditReminderDestination.route}/${it}" )
                }
            )
        }
        composable(route = NewReminderDestination.route) {
            LaunchedEffect(Unit) { onTitleResChanged(NewReminderDestination.titleRes) }
            ReminderEditScreen(navigateBack = navigateBackHome)
        }
        composable(
            route = EditReminderDestination.routeWithArgs,
            arguments = EditReminderDestination.arguments
        ) {
            LaunchedEffect(Unit) { onTitleResChanged(EditReminderDestination.titleRes) }
            ReminderEditScreen(navigateBack = navigateBackHome)
        }
    }
}