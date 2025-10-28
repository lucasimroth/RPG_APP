package cs.up.edu.br.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipamento",
    foreignKeys = [
        ForeignKey(
            entity = PersonagemEntity::class,
            parentColumns = ["id"],
            childColumns = ["personagemOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EquipamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personagemOwnerId: Long,
    val nome: String,
    val carga: Int,
    val dano: String?,
    val bonusCA: Int?,
    val tipoEquipamento: String
)