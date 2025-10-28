package cs.up.edu.br.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cs.up.edu.br.data.PersonagemRepository
import cs.up.edu.br.logic.EstiloDeRolagem
import cs.up.edu.br.logic.GeradorDeAtributos
import cs.up.edu.br.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado para a tela de lista (continua igual)
data class PersonagemListUiState(
    val personagens: List<Personagem> = emptyList(),
    val isLoading: Boolean = false
)

// NOVO: Estado para a tela de criação
data class CreatePersonagemUiState(
    val nome: String = "",
    val raca: String = "",
    val classe: String = "",
    val alinhamento: String = "",
    val estiloRolagem: EstiloDeRolagem = EstiloDeRolagem.CLASSICO,
    // Atributos rolados
    val forca: Int = 0,
    val destreza: Int = 0,
    val constituicao: Int = 0,
    val inteligencia: Int = 0,
    val sabedoria: Int = 0,
    val carisma: Int = 0,
    val podeSalvar: Boolean = false // Para habilitar o botão de salvar
)

class PersonagemViewModel(private val repository: PersonagemRepository) : ViewModel() {

    // --- ESTADO DA LISTA ---
    private val _listState = MutableStateFlow(PersonagemListUiState())
    val listState: StateFlow<PersonagemListUiState> = _listState.asStateFlow()

    // --- ESTADO DA CRIAÇÃO ---
    private val _createState = MutableStateFlow(CreatePersonagemUiState())
    val createState: StateFlow<CreatePersonagemUiState> = _createState.asStateFlow()

    // --- ESTADO DA FICHA (DETALHE) ---
    private val _selectedPersonagem = MutableStateFlow<Personagem?>(null)
    val selectedPersonagem: StateFlow<Personagem?> = _selectedPersonagem.asStateFlow()

    // --- Listas de Opções ---
    val racasOptions = listOf(Humano().nome, Elfo().nome, Anao().nome, Halfling().nome)
    val classesOptions = listOf(Guerreiro().nome, Mago().nome, Ladrao().nome)
    val estilosRolagemOptions = EstiloDeRolagem.values().toList()

    // --- LÓGICA DA LISTA ---
    fun buscarPersonagens() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }
            val todosPersonagens = repository.getAllPersonagens()
            _listState.update {
                it.copy(
                    isLoading = false,
                    personagens = todosPersonagens
                )
            }
        }
    }

    // --- LÓGICA DA FICHA (DETALHE) ---
    fun buscarPersonagemPorId(id: Long) {
        viewModelScope.launch {
            _selectedPersonagem.value = repository.getPersonagem(id)
        }
    }

    // --- LÓGICA DA CRIAÇÃO ---
    fun onCreationStateChange(
        nome: String? = null,
        alinhamento: String? = null,
        raca: String? = null,
        classe: String? = null,
        estilo: EstiloDeRolagem? = null
    ) {
        _createState.update {
            it.copy(
                nome = nome ?: it.nome,
                alinhamento = alinhamento ?: it.alinhamento,
                raca = raca ?: it.raca,
                classe = classe ?: it.classe,
                estiloRolagem = estilo ?: it.estiloRolagem
            )
        }
        validarFormulario()
    }

    fun rolarAtributos() {
        val valores = GeradorDeAtributos.gerarValores(_createState.value.estiloRolagem)
        _createState.update {
            it.copy(
                forca = valores[0],
                destreza = valores[1],
                constituicao = valores[2],
                inteligencia = valores[3],
                sabedoria = valores[4],
                carisma = valores[5]
            )
        }
        validarFormulario()
    }

    private fun validarFormulario() {
        val state = _createState.value
        val camposBasicosPreenchidos = state.nome.isNotBlank() &&
                state.raca.isNotBlank() &&
                state.classe.isNotBlank() &&
                state.alinhamento.isNotBlank()
        val atributosRolados = state.forca > 0 // Checa se pelo menos um atributo foi rolado

        _createState.update { it.copy(podeSalvar = camposBasicosPreenchidos && atributosRolados) }
    }

    fun salvarPersonagemCompleto() {
        viewModelScope.launch {
            val state = _createState.value
            if (!state.podeSalvar) return@launch

            val atributos = Atributos(
                forca = state.forca, destreza = state.destreza, constituicao = state.constituicao,
                inteligencia = state.inteligencia, sabedoria = state.sabedoria, carisma = state.carisma
            )
            val raca = when(state.raca) {
                "Elfo" -> Elfo()
                "Anao" -> Anao()
                "Halfling" -> Halfling()
                else -> Humano()
            }
            val classe = when(state.classe) {
                "Mago" -> Mago()
                "Ladrao" -> Ladrao()
                else -> Guerreiro()
            }

            val novoPersonagem = Personagem(
                nome = state.nome,
                raca = raca,
                classe = classe,
                atributos = atributos,
                nivel = 1,
                alinhamento = state.alinhamento,
                inventario = mutableListOf()
            )

            repository.salvarPersonagem(novoPersonagem)
        }
    }

    fun limparFormularioCriacao() {
        _createState.value = CreatePersonagemUiState()
    }
}
/**
 * Esta é a Fábrica que ensina o Android a criar nosso PersonagemViewModel,
 * já que ele tem uma dependência (o Repository) no construtor.
 */
class PersonagemViewModelFactory(private val repository: PersonagemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonagemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonagemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}