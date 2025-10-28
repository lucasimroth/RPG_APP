package cs.up.edu.br.data

import androidx.room.*
import cs.up.edu.br.data.EquipamentoEntity
import cs.up.edu.br.data.PersonagemEntity
import cs.up.edu.br.models.*

// Classe de Relação que o Room usará para nos entregar o Personagem e seu Inventário
data class PersonagemComInventario(
    @Embedded
    val personagem: PersonagemEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "personagemOwnerId"
    )
    val inventario: List<EquipamentoEntity>
)

// Objeto singleton para fazer as conversões
object PersonagemMapper {

    /**
     * Converte da Entidade (DB) para o Modelo de Domínio (App)
     */
    fun toDomain(personagemComInventario: PersonagemComInventario): Personagem {
        val entity = personagemComInventario.personagem

        // 1. Reconstrói a Classe
        val classe: Classe = when (entity.classeNome) {
            "Guerreiro" -> Guerreiro()
            "Mago" -> Mago()
            "Ladrao" -> Ladrao()
            else -> throw IllegalArgumentException("Classe desconhecida: ${entity.classeNome}")
        }

        // 2. Reconstrói a Raça
        val raca: Raca = when (entity.racaNome) {
            "Humano" -> Humano()
            "Elfo" -> Elfo()
            "Anao" -> Anao()
            "Halfling" -> Halfling()
            else -> throw IllegalArgumentException("Raça desconhecida: ${entity.racaNome}")
        }

        // 3. Reconstrói o Inventário
        val inventario: MutableList<Equipamento> = personagemComInventario.inventario.map { itemEntity ->
            when (itemEntity.tipoEquipamento) {
                "Arma" -> Arma(itemEntity.nome, itemEntity.carga, itemEntity.dano!!)
                "Armadura" -> Armadura(itemEntity.nome, itemEntity.carga, itemEntity.bonusCA!!)
                "Escudo" -> Escudo(itemEntity.nome, itemEntity.carga, itemEntity.bonusCA!!)
                "ItemGeral" -> ItemGeral(itemEntity.nome, itemEntity.carga)
                else -> throw IllegalArgumentException("Equipamento desconhecido: ${itemEntity.tipoEquipamento}")
            }
        }.toMutableList()

        // 4. Monta o Personagem
        return Personagem(
            id = personagemComInventario.personagem.id,
            nome = entity.nome,
            raca = raca,
            classe = classe,
            atributos = Atributos(
                forca = entity.atributos.forca,
                destreza = entity.atributos.destreza,
                constituicao = entity.atributos.constituicao,
                inteligencia = entity.atributos.inteligencia,
                sabedoria = entity.atributos.sabedoria,
                carisma = entity.atributos.carisma
            ),
            nivel = entity.nivel,
            alinhamento = entity.alinhamento,
            inventario = inventario
        ).apply {
            // Se o ID do DB for importante para o modelo de domínio, você pode guardá-lo
            // ex: var id: Long = entity.id
        }
    }

    /**
     * Converte do Modelo de Domínio (App) para a Entidade (DB)
     */
    fun toEntity(personagem: Personagem, personagemId: Long = 0): PersonagemComInventario {
        // 1. Cria a Entidade do Personagem
        val personagemEntity = PersonagemEntity(
            id = personagem.id, // 0 para novo, >0 para update
            nome = personagem.nome,
            nivel = personagem.nivel,
            alinhamento = personagem.alinhamento,
            atributos = AtributosEntity(
                forca = personagem.atributos.forca,
                destreza = personagem.atributos.destreza,
                constituicao = personagem.atributos.constituicao,
                inteligencia = personagem.atributos.inteligencia,
                sabedoria = personagem.atributos.sabedoria,
                carisma = personagem.atributos.carisma
            ),
            racaNome = personagem.raca.nome, // Salva apenas o nome
            classeNome = personagem.classe.nome // Salva apenas o nome
        )

        // 2. Cria as Entidades do Inventário
        val inventarioEntity = personagem.inventario.map { equipamento ->
            EquipamentoEntity(
                // O ID do item e do dono serão definidos pelo DAO
                personagemOwnerId = 0, // Será preenchido pelo DAO
                nome = equipamento.nome,
                carga = equipamento.carga,

                // Mapeia os campos específicos
                dano = (equipamento as? Arma)?.dano,
                bonusCA = (equipamento as? Armadura)?.bonusCA ?: (equipamento as? Escudo)?.bonusCA,

                // Salva o tipo para sabermos reconstruir depois
                tipoEquipamento = when (equipamento) {
                    is Arma -> "Arma"
                    is Armadura -> "Armadura"
                    is Escudo -> "Escudo"
                    is ItemGeral -> "ItemGeral"
                }
            )
        }

        return PersonagemComInventario(personagemEntity, inventarioEntity)
    }
}