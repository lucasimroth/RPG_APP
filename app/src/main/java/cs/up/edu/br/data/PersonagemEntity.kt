package cs.up.edu.br.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personagem")
data class PersonagemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val nivel: Int,
    val alinhamento: String,

    // Aninha os campos de Atributos diretamente na tabela personagem
    // (forca, destreza, constituicao, etc.)
    @Embedded
    val atributos: AtributosEntity,

    // Armazenamos apenas o nome da Raça e Classe.
    // O Mapeador cuidará da conversão.
    val racaNome: String,
    val classeNome: String
)