package cs.up.edu.br.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cs.up.edu.br.ui.viewmodel.PersonagemListUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.LaunchedEffect
import cs.up.edu.br.ui.viewmodel.PersonagemViewModel
import cs.up.edu.br.models.Personagem
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonagemListScreen(
    uiState: PersonagemListUiState,
    viewModel: PersonagemViewModel,
    onPersonagemClick: (Personagem) -> Unit,
    onAddPersonagem: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.buscarPersonagens()
        }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Personagens") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPersonagem) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Personagem")
            }
        }
    ) { innerPadding ->

        // Aplicar o padding interno fornecido pelo Scaffold
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                // Se estiver carregando, mostrar um indicador
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.personagens.isEmpty()) {
                // Se não estiver carregando E a lista estiver vazia
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum personagem encontrado.\nClique no '+' para criar um novo.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Se tiver personagens, mostrar a lista
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.personagens) { personagem ->
                        PersonagemCard(
                            nome = personagem.nome,
                            raca = personagem.raca.nome,
                            classe = personagem.classe.nome,
                            onClick = {
                                // 2. CHAME A AÇÃO DE CLIQUE
                                onPersonagemClick(personagem)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Um Card simples para exibir as informações do personagem.
 */
@Composable
fun PersonagemCard(nome: String, raca: String, classe: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = nome,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$raca $classe",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}