package cs.up.edu.br.data

import androidx.room.*

@Dao
interface PersonagemDao {

    /**
     * Insere um personagem e seus equipamentos de forma transacional
     */
    @Transaction
    suspend fun insertPersonagemCompleto(personagemComInventario: PersonagemComInventario) {
        // 1. Insere o personagem e pega seu ID gerado
        val personagemId = insertPersonagem(personagemComInventario.personagem)

        // 2. Atualiza os itens do inventário com o ID do personagem
        val equipamentosComOwnerId = personagemComInventario.inventario.map {
            it.copy(personagemOwnerId = personagemId)
        }

        // 3. Insere os equipamentos
        insertEquipamentos(equipamentosComOwnerId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonagem(personagem: PersonagemEntity): Long // Retorna o ID do personagem inserido

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipamentos(equipamentos: List<EquipamentoEntity>)

    /**
     * Busca um personagem completo pelo seu ID.
     * O @Transaction garante que o Room busque o Personagem e seus Equipamentos (Relação)
     * de forma atômica.
     */
    @Transaction
    @Query("SELECT * FROM personagem WHERE id = :id")
    suspend fun getPersonagemCompleto(id: Long): PersonagemComInventario?

    /**
     * Busca todos os personagens completos.
     */
    @Transaction
    @Query("SELECT * FROM personagem")
    suspend fun getAllPersonagensCompletos(): List<PersonagemComInventario>

    @Delete
    suspend fun deletePersonagem(personagem: PersonagemEntity)

}