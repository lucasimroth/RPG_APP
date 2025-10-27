package cs.up.edu.br.models

class Personagem (
    val nome: String,
    var raca: Raca,
    var classe: Classe,
    var atributos: Atributos,
    var nivel: Int = 1,
    var alinhamento: String,
    val inventario: MutableList<Equipamento> = mutableListOf()
){
    val pontosDeVida: Int = classe.dadoDeVida + atributos.modificadorConstituicao

    val classeDeArmadura: Int
        get(){
            val bonusArmadura = inventario.filterIsInstance<Armadura>().firstOrNull()?.bonusCA ?: 0
            val bonusEscudo = inventario.filterIsInstance<Escudo>().firstOrNull()?.bonusCA ?: 0
            return 10 + atributos.modificadorDestreza + bonusArmadura + bonusEscudo
        }
    val baseDeAtaqueCorpoACorpo: Int
    get() = classe.obterBA(nivel) + atributos.modificadorForca

    val baseDeAtaqueADistancia: Int
    get() = classe.obterBA(nivel) + atributos.modificadorDestreza

    val jogadaDeProtecaoBase: Int
    get() = classe.obterJP(nivel)

    val jogadaDeProtecaoDestreza: Int
    get() = jogadaDeProtecaoBase + atributos.modificadorDestreza

    val jogadaDeProtecaoConstituicao: Int
    get() = jogadaDeProtecaoBase + atributos.modificadorConstituicao

    val jogadaDeProtecaoSabedoria: Int
    get() = jogadaDeProtecaoBase + atributos.modificadorSabedoria
}