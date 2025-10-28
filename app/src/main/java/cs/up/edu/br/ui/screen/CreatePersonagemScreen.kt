package cs.up.edu.br.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cs.up.edu.br.logic.EstiloDeRolagem
import cs.up.edu.br.ui.viewmodel.PersonagemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePersonagemScreen(
    viewModel: PersonagemViewModel,
    onNavigateBack: () -> Unit
) {
    // 1. Coletar o estado do formulário do ViewModel
    val uiState by viewModel.createState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Novo Personagem") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Usamos LazyColumn para o formulário ser rolável
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text("Informações Básicas", style = MaterialTheme.typography.titleMedium)
            }

            // --- NOME ---
            item {
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = { viewModel.onCreationStateChange(nome = it) },
                    label = { Text("Nome do Personagem") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
            }

            // --- RAÇA ---
            item {
                DropdownMenuBox(
                    label = "Raça",
                    options = viewModel.racasOptions,
                    selectedOption = uiState.raca,
                    onOptionSelected = { viewModel.onCreationStateChange(raca = it) }
                )
            }

            // --- CLASSE ---
            item {
                DropdownMenuBox(
                    label = "Classe",
                    options = viewModel.classesOptions,
                    selectedOption = uiState.classe,
                    onOptionSelected = { viewModel.onCreationStateChange(classe = it) }
                )
            }

            // --- ALINHAMENTO ---
            item {
                OutlinedTextField(
                    value = uiState.alinhamento,
                    onValueChange = { viewModel.onCreationStateChange(alinhamento = it) },
                    label = { Text("Alinhamento (ex: Leal e Bom)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            // --- ROLAGEM DE ATRIBUTOS ---
            item {
                Text("Atributos", style = MaterialTheme.typography.titleMedium)
            }

            item {
                DropdownMenuBox(
                    label = "Estilo de Rolagem",
                    options = viewModel.estilosRolagemOptions.map { it.name },
                    selectedOption = uiState.estiloRolagem.name,
                    onOptionSelected = {
                        viewModel.onCreationStateChange(estilo = EstiloDeRolagem.valueOf(it))
                    }
                )
            }

            item {
                Button(
                    onClick = { viewModel.rolarAtributos() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ROLAR ATRIBUTOS")
                }
            }

            // --- EXIBIÇÃO DOS ATRIBUTOS ---
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttributeTextField(label = "FOR", value = uiState.forca.toString(), Modifier.weight(1f))
                    AttributeTextField(label = "DES", value = uiState.destreza.toString(), Modifier.weight(1f))
                    AttributeTextField(label = "CON", value = uiState.constituicao.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttributeTextField(label = "INT", value = uiState.inteligencia.toString(), Modifier.weight(1f))
                    AttributeTextField(label = "SAB", value = uiState.sabedoria.toString(), Modifier.weight(1f))
                    AttributeTextField(label = "CAR", value = uiState.carisma.toString(), Modifier.weight(1f))
                }
            }

            // --- BOTÃO SALVAR ---
            item {
                Button(
                    onClick = {
                        viewModel.salvarPersonagemCompleto()
                        onNavigateBack()
                    },
                    enabled = uiState.podeSalvar, // Habilitado só se o form for válido
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SALVAR PERSONAGEM")
                }
            }
        }
    }
}

// Composable auxiliar para os campos de Atributo
@Composable
fun AttributeTextField(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier
    )
}

// Composable auxiliar para os Menus Dropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}