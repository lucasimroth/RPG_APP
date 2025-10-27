package cs.up.edu.br.logic

import kotlin.random.Random

//classe para dados constantes
enum class EstiloDeRolagem{
    CLASSICO, AVENTUREIRO, HEROICO
}

//é um objeto unico
object GeradorDeAtributos{
    fun gerarValores(estilo: EstiloDeRolagem):List<Int>{
        return when(estilo){
            EstiloDeRolagem.CLASSICO -> {
                List(6){rolarDados(3,6)}
            }
            EstiloDeRolagem.AVENTUREIRO -> {
                List(6){rolarDados(3,6)}
            }

            EstiloDeRolagem.HEROICO ->{
                List(6){rolarDados(4, 6, descartarMenor = true)}
            }
        }
    }

    private fun rolarDados(numeroDeDados: Int, lados: Int, descartarMenor: Boolean = false): Int{
        val rolagens = MutableList(numeroDeDados){Random.nextInt(1, lados + 1)}
        if (descartarMenor && rolagens.size > 1){
            rolagens.remove(rolagens.minOrNull())
        }
        return rolagens.sum()
    }
}