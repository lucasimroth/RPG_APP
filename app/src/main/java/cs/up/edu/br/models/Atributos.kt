package cs.up.edu.br.models

import kotlin.Int

//classe de dados que faz os atributos serem como um
data class Atributos(
    val forca: Int,
    val destreza: Int,
    val constituicao: Int,
    val inteligencia: Int,
    val sabedoria: Int,
    val carisma: Int
){
    val modificadorForca: Int = calcularModificador(forca)
    val modificadorDestreza: Int = calcularModificador(destreza)
    val modificadorConstituicao: Int = calcularModificador(constituicao)
    val modificadorInteligencia: Int = calcularModificador(inteligencia)
    val modificadorSabedoria: Int = calcularModificador(sabedoria)
    val modificadorCarisma: Int = calcularModificador(carisma)

    //estabelece os modificadores com base no status correspondente
    private fun calcularModificador(valor: Int): Int{
        return when(valor){
            in 2..3 -> -3
            in 4.. 5 -> -2
            in 6..8 -> -1
            in 13..14 -> 1
            in 15..16 -> 2
            in 17..18 -> 3
            in 19..20 -> 4
            else -> 0
        }
    }
}
