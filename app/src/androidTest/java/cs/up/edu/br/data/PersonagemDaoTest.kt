package cs.up.edu.br.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cs.up.edu.br.models.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PersonagemDaoTest {

    private lateinit var personagemDao: PersonagemDao
    private lateinit var db: AppDatabase

    // --- 1. SETUP (@Before) ---
    // Isto roda ANTES de cada teste
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usando o banco de dados em memória
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        personagemDao = db.personagemDao()
    }

    // --- 2. TEARDOWN (@After) ---
    // Isto roda DEPOIS de cada teste
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close() // Fecha o banco em memória
    }

    // --- 3. O TESTE (@Test) ---
    @Test
    @Throws(Exception::class)
    fun insertAndGetPersonagemCompleto() = runBlocking {
        // --- ARRANGE (Organizar) ---
        // 1. Criar um personagem (Model de Domínio)
        val atributos = Atributos(forca = 14, destreza = 12, constituicao = 16, inteligencia = 8, sabedoria = 10, carisma = 10)
        val espada = Arma(nome = "Espada Longa", carga = 2, dano = "1d8")
        val armadura = Armadura(nome = "Cota de Malha", carga = 4, bonusCA = 4)

        val personagemOriginal = Personagem(
            nome = "Grog",
            raca = Anao(), // Model de Raça
            classe = Guerreiro(), // Model de Classe
            atributos = atributos,
            nivel = 3,
            alinhamento = "Caótico Bom",
            inventario = mutableListOf(espada, armadura)
        )

        // 2. Mapear para Entidade (usando o Mapper)
        val personagemParaSalvar = PersonagemMapper.toEntity(personagemOriginal)

        // --- ACT (Agir) ---
        // 3. Salvar no banco (usando o DAO)
        personagemDao.insertPersonagemCompleto(personagemParaSalvar)

        // 4. Buscar do banco (usando o DAO)
        // Usamos getAll pois não sabemos o ID auto-gerado
        val todosPersonagens = personagemDao.getAllPersonagensCompletos()
        val personagemRecuperado = todosPersonagens.firstOrNull()

        // --- ASSERT (Verificar) ---
        // 5. Verificar se tudo foi salvo e lido corretamente
        assertNotNull(personagemRecuperado)

        // Verifica o PersonagemEntity
        assertEquals("Grog", personagemRecuperado!!.personagem.nome)
        assertEquals(3, personagemRecuperado.personagem.nivel)
        assertEquals(16, personagemRecuperado.personagem.atributos.constituicao)

        // Verifica se o Mapeamento de Raca/Classe funcionou
        assertEquals("Anao", personagemRecuperado.personagem.racaNome)
        assertEquals("Guerreiro", personagemRecuperado.personagem.classeNome)

        // Verifica o Inventário (Relação)
        assertEquals(2, personagemRecuperado.inventario.size)
        val itemEspada = personagemRecuperado.inventario.find { it.nome == "Espada Longa" }
        assertNotNull(itemEspada)
        assertEquals("1d8", itemEspada?.dano)
        assertEquals(null, itemEspada?.bonusCA) // Verifica se não misturou dados

        val itemArmadura = personagemRecuperado.inventario.find { it.nome == "Cota de Malha" }
        assertNotNull(itemArmadura)
        assertEquals(4, itemArmadura?.bonusCA)
        assertEquals(null, itemArmadura?.dano)
    }
}