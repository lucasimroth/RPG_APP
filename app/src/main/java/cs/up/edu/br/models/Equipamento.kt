package cs.up.edu.br.models

sealed class Equipamento(
    val nome: String,
    val carga: Int
)
class Arma(
    nome: String,
    carga: Int,
    val dano: String
): Equipamento(nome, carga)

class Armadura(
    nome: String,
    carga: Int,
    val bonusCA: Int
): Equipamento(nome, carga)

class Escudo(nome:String, carga: Int, val bonusCA:Int) : Equipamento(nome, carga)

class ItemGeral(
    nome: String,
    carga: Int
): Equipamento(nome, carga)