package cs.up.edu.br.logic

import cs.up.edu.br.models.Personagem
import kotlin.random.Random
import android.util.Log

// Modelo simples para um Inimigo
data class Inimigo(
    val nome: String,
    var pontosDeVida: Int,
    val classeDeArmadura: Int,
    val dano: String, // Ex: "1d6"
    val baseDeAtaque: Int
)

object SimuladorDeBatalha {

    private const val TAG = "SimuladorDeBatalha"

    /**
     * Simula um combate simples entre um Personagem e um Inimigo.
     * @param personagem O Personagem do jogador (Modelo de domínio, não modificado aqui).
     * @param inimigo O Inimigo a ser enfrentado.
     * @return true se o personagem vencer, false caso contrário (morrer).
     */
    fun simularBatalha(personagem: Personagem, inimigo: Inimigo): Boolean {
        Log.d(TAG, "Iniciando batalha: ${personagem.nome} vs ${inimigo.nome}")

        // Clona os PVs para simular a luta sem alterar o modelo original durante a função
        var pvPersonagem = personagem.pontosDeVida
        var pvInimigo = inimigo.pontosDeVida

        while (pvPersonagem > 0 && pvInimigo > 0) {
            // --- Turno do Personagem ---
            if (personagem.atacar(inimigo.classeDeArmadura)) {
                val danoPersonagem = rolarDanoPersonagem(personagem)
                pvInimigo -= danoPersonagem
                Log.d(TAG, "${personagem.nome} acerta ${inimigo.nome} causando $danoPersonagem de dano. PV Inimigo: $pvInimigo")
            } else {
                Log.d(TAG, "${personagem.nome} erra o ataque.")
            }

            if (pvInimigo <= 0) break // Inimigo derrotado

            // --- Turno do Inimigo ---
            if (inimigo.atacar(personagem.classeDeArmadura)) {
                val danoInimigo = rolarDano(inimigo.dano)
                pvPersonagem -= danoInimigo
                Log.d(TAG, "${inimigo.nome} acerta ${personagem.nome} causando $danoInimigo de dano. PV Personagem: $pvPersonagem")
            } else {
                Log.d(TAG, "${inimigo.nome} erra o ataque.")
            }
        }

        Log.d(TAG, "Batalha finalizada. Personagem PV final: $pvPersonagem. Inimigo PV final: $pvInimigo")

        // Retorna se o personagem venceu (PVs > 0)
        return pvPersonagem > 0
    }

    // Rola um 1d20 para ataque (assume Corpo-a-Corpo como padrão)
    private fun Personagem.atacar(caAlvo: Int): Boolean {
        val bonusAtaque = this.baseDeAtaqueCorpoACorpo
        val d20 = Random.nextInt(1, 21)
        val acerto = d20 + bonusAtaque
        return acerto >= caAlvo
    }

    // Rola o dano do Personagem, usando a primeira arma ou 1d4 (desarmado)
    private fun rolarDanoPersonagem(personagem: Personagem): Int {
        val arma = personagem.inventario.filterIsInstance<cs.up.edu.br.models.Arma>().firstOrNull()
        val danoString = arma?.dano ?: "1d4"
        return rolarDano(danoString)
    }

    // Rola dados de dano baseados em uma string (ex: "1d8")
    private fun rolarDano(danoString: String): Int {
        val match = "(\\d+)d(\\d+)".toRegex().find(danoString)
        if (match != null) {
            val (numDadosStr, ladosStr) = match.destructured
            val numDados = numDadosStr.toIntOrNull() ?: 1
            val lados = ladosStr.toIntOrNull() ?: 6
            return (1..numDados).sumOf { Random.nextInt(1, lados + 1) }
        }
        return 1
    }

    // Rola um 1d20 para ataque do Inimigo
    private fun Inimigo.atacar(caAlvo: Int): Boolean {
        val d12 = Random.nextInt(1, 13)
        val acerto = d12 + this.baseDeAtaque
        return acerto >= caAlvo
    }
}