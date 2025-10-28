package cs.up.edu.br.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipamento",
    foreignKeys = [
        ForeignKey(
            entity = PersonagemEntity::class,
            parentColumns = ["id"], // <-- Correto
            childColumns = ["personagemOwnerId"], // <-- Correto
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EquipamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // O ID do personagem dono deste item
    val personagemOwnerId: Long,

    val nome: String,
    val carga: Int,

    // Campos das subclasses (Arma, Armadura, etc.)
    // Usamos nullables pois um item geral não tem dano, por exemplo.
    val dano: String?,
    val bonusCA: Int?,

    // Um campo para sabermos que tipo de equipamento é (Arma, Armadura, Escudo, ItemGeral)
    val tipoEquipamento: String
)