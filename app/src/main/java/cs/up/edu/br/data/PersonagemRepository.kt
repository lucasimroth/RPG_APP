package cs.up.edu.br.data
import cs.up.edu.br.models.Personagem

class PersonagemRepository(private val personagemDao: PersonagemDao) {

    /**
     * Salva um personagem (modelo de domínio) no banco de dados.
     */
    suspend fun salvarPersonagem(personagem: Personagem) {
        val personagemCompleto = PersonagemMapper.toEntity(personagem)
        personagemDao.insertPersonagemCompleto(personagemCompleto)
    }

    /**
     * Busca um personagem (modelo de domínio) pelo ID.
     */
    suspend fun getPersonagem(id: Long?): Personagem? {
        return id?.let { nonNullId ->
            val entity = personagemDao.getPersonagemCompleto(nonNullId)
            entity?.let { PersonagemMapper.toDomain(it) }
        }
    }

    /**
     * Busca todos os personagens (modelos de domínio).
     */
    suspend fun getAllPersonagens(): List<Personagem> {
        return personagemDao.getAllPersonagensCompletos().map {
            PersonagemMapper.toDomain(it)
        }
    }

    /**
     * Deleta um personagem (modelo de domínio) do banco de dados.
     */
    suspend fun deletarPersonagem(personagem: Personagem) {
        // Mapeia o modelo de domínio para a entidade para poder deletar
        val personagemEntity = PersonagemMapper.toEntity(personagem).personagem
        personagemDao.deletePersonagem(personagemEntity)
    }

}