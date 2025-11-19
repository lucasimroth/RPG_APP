package cs.up.edu.br

import android.os.Bundle
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import android.content.Intent
import android.os.Build


class MainActivity : ComponentActivity() {

    // para solicitar permissão de notificação
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permissions", "POST_NOTIFICATIONS concedida.")
        } else {
            Log.w("Permissions", "POST_NOTIFICATIONS negada. Notificações de morte não aparecerão.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicita permissão de notificação se necessário
        requestNotificationPermission()

        val repository = (application as MainApplication).repository
        val viewModel = PersonagemViewModel(repository)

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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicita a permissão em tempo de execução
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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
    val listUiState by viewModel.listState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val onStartBattle: (Long) -> Unit = { personagemId ->
        val intent = Intent(context, BattleService::class.java).apply {
            putExtra(BattleService.CHARACTER_ID_EXTRA, personagemId)
        }

        // Inicia o serviço em primeiro plano
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        navController.popBackStack("personagem_list", inclusive = false)
    }

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
                    },
                    onStartBattle = onStartBattle
                )
            }
        }
    }
}