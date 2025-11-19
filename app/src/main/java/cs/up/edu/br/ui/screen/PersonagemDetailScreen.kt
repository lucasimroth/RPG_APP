package cs.up.edu.br.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cs.up.edu.br.models.Personagem
import cs.up.edu.br.ui.viewmodel.PersonagemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonagemDetailScreen(
    viewModel: PersonagemViewModel,
    onNavigateBack: () -> Unit,
    onStartBattle: (Long) -> Unit
) {
    // Coleta o personagem selecionado do ViewModel
    val personagem by viewModel.selectedPersonagem.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(personagem?.nome ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Se o personagem não for nulo, mostra a ficha
        personagem?.let { p ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- CABEÇALHO ---
                item {
                    Text(
                        text = "${p.raca.nome} ${p.classe.nome} - Nível ${p.nivel}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = p.alinhamento,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // --- ATRIBUTOS ---
                item {
                    Text("Atributos", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    AtributosGrid(p = p)
                }

                // --- ESTATÍSTICAS DE COMBATE ---
                item {
                    Text("Combate", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Pontos de Vida (PV)", p.pontosDeVida.toString())
                    InfoRow("Classe de Armadura (CA)", p.classeDeArmadura.toString())
                    InfoRow("Ataque Corpo-a-Corpo", "+${p.baseDeAtaqueCorpoACorpo}")
                    InfoRow("Ataque à Distância", "+${p.baseDeAtaqueADistancia}")
                }

                // --- JOGADAS DE PROTEÇÃO ---
                item {
                    Text("Jogadas de Proteção", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("JP Destreza", "+${p.jogadaDeProtecaoDestreza}")
                    InfoRow("JP Constituição", "+${p.jogadaDeProtecaoConstituicao}")
                    InfoRow("JP Sabedoria", "+${p.jogadaDeProtecaoSabedoria}")
                }

                //Botão de Batalha
                item {
                    Button(
                        onClick = {
                            onStartBattle(p.id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("INICIAR BATALHA CONTRA ORC (Service)")
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        } ?: run {
            // Se o personagem for nulo (carregando)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

// Composable auxiliar para a grade de atributos
@Composable
fun AtributosGrid(p: Personagem) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AttrDisplay("FOR", p.atributos.forca, p.atributos.modificadorForca, Modifier.weight(1f))
        AttrDisplay("DES", p.atributos.destreza, p.atributos.modificadorDestreza, Modifier.weight(1f))
        AttrDisplay("CON", p.atributos.constituicao, p.atributos.modificadorConstituicao, Modifier.weight(1f))
    }
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AttrDisplay("INT", p.atributos.inteligencia, p.atributos.modificadorInteligencia, Modifier.weight(1f))
        AttrDisplay("SAB", p.atributos.sabedoria, p.atributos.modificadorSabedoria, Modifier.weight(1f))
        AttrDisplay("CAR", p.atributos.carisma, p.atributos.modificadorCarisma, Modifier.weight(1f))
    }
}

// Composable auxiliar para exibir um atributo e seu modificador
@Composable
fun AttrDisplay(label: String, valor: Int, mod: Int, modifier: Modifier = Modifier) {
    val modSign = if (mod > 0) "+$mod" else mod.toString()
    OutlinedCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(valor.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("($modSign)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// Composable auxiliar para linhas de informação (ex: PV: 10)
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
    Divider(color = Color.Gray.copy(alpha = 0.3f))
}