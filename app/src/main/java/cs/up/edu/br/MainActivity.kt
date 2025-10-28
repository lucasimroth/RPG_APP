package cs.up.edu.br

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cs.up.edu.br.ui.screen.CreatePersonagemScreen
import cs.up.edu.br.ui.screen.PersonagemListScreen
import cs.up.edu.br.ui.theme.CriadorPersonagemTheme
import cs.up.edu.br.ui.viewmodel.PersonagemViewModel
import cs.up.edu.br.ui.viewmodel.PersonagemViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cs.up.edu.br.ui.screen.PersonagemDetailScreen
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriadorPersonagemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Obter o ViewModel (ele será compartilhado entre as telas)
                    val application = LocalContext.current.applicationContext as MainApplication
                    val viewModel: PersonagemViewModel = viewModel(
                        factory = PersonagemViewModelFactory(application.repository)
                    )

                    // 2. Chamar o Composable principal do App
                    RpgApp(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * O Composable raiz que gerencia a navegação.
 */
@Composable
fun RpgApp(viewModel: PersonagemViewModel) {
    val navController = rememberNavController()
    // Renomeie 'uiState' para 'listUiState' para maior clareza
    val listUiState by viewModel.listState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = "personagem_list") {

        // --- Rota da Lista ---
        composable(route = "personagem_list") {
            PersonagemListScreen(
                uiState = listUiState,
                viewModel = viewModel,
                onAddPersonagem = {
                    // Limpa o formulário antes de navegar
                    viewModel.limparFormularioCriacao()
                    navController.navigate("create_personagem")
                },
                // Define o que acontece ao clicar em um card
                onPersonagemClick = { personagem ->
                    navController.navigate("personagem_detail/${personagem.id}")
                }
            )
        }

        // --- Rota de Criação ---
        composable(route = "create_personagem") {
            CreatePersonagemScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- ROTA DA FICHA (DETALHE) ---
        composable(
            route = "personagem_detail/{personagemId}",
            arguments = listOf(navArgument("personagemId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Pega o ID da rota
            val personagemId = backStackEntry.arguments?.getLong("personagemId")

            // Garante que o ID não seja nulo e busca o personagem
            LaunchedEffect(personagemId) {
                if (personagemId != null) {
                    viewModel.buscarPersonagemPorId(personagemId)
                }
            }

            if (personagemId != null) {
                PersonagemDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}