package com.example.diaria_do_motorista.data.db.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaria_do_motorista.data.db.session.AuthStateViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.home.HomeScreen
import com.example.diaria_do_motorista.ui.theme.feature.login.home.HomeViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.login.LoginScreen
import com.example.diaria_do_motorista.ui.theme.feature.login.login.LoginViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.relatorio.RelatorioScreen
import com.example.diaria_do_motorista.ui.theme.feature.login.relatorio.RelatorioViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit.UsuarioEditScreen
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list.UsuarioEditViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list.UsuariosListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authStateViewModel: AuthStateViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by authStateViewModel.currentUser.collectAsState()
    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) Routes.Home.route else Routes.Login.route,
        modifier = modifier
    ) {
        //LOGIN
        composable(Routes.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel: viewModel,
                onLoginSuccess = { usuario ->
                    authStateViewModel.setUser(usuario)
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { /* Navegar para tela de registro */ }
            )
        }
        //HOME
        composable(Routes.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToUsuarios = { navController.navigate(Routes.UsuariosList.route) },
                onNavigateToRelatorios = { navController.navigate(Routes.Relatorios.route) },
                onNavigateToTransportadoras = { navController.navigate(Routes.Transportadoras.route) },
                onNavigateToVeiculos = { navController.navigate(Routes.Veiculos.route) },
                onLogout = {
                    authStateViewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        //Usuário List
        composable(Routes.UsuariosList.route) {
            val viewModel: UsuariosListViewModel = hiltViewModel()
            UsuariosListScreen(
                viewModel = viewModel,
                onNavigateToEdit = { usuarioId ->
                    navController.navigate(Routes.UsuarioEdit.passId(usuarioId))
                },
                onNavigateToDetails = { usuarioId ->
                    navController.navigate(Routes.UsuarioDetails.passId(usuarioId))
                }
            )
        }
        // Usuário Edit
        composable(
            route = Routes.UsuarioEdit.route,
            arguments = listOf(navArgument("usuarioId") { defaultValue = "new" })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: "new"
            val viewModel: UsuarioEditViewModel = hiltViewModel()
            UsuarioEditScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Relatórios
        composable(Routes.Relatorios.route) {
            val viewModel: RelatorioViewModel = hiltViewModel()
            RelatorioScreen(viewModel = viewModel)
        }
    }
}


