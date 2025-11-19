package cs.up.edu.br.models

sealed class Classe(
    val nome: String,
    val dadoDeVida: Int
){
    abstract fun obterHabilidadeDeClasse(nivel: Int): List<String>
    abstract fun podeUsarArmadura(tipo: String): Boolean
    abstract fun obterJP(nivel: Int): Int
    abstract fun obterBA(nivel: Int): Int

    abstract fun obterEquipamentoInicial(): MutableList<Equipamento>
}

class Guerreiro : Classe("Guerreiro", 10){
    override fun obterHabilidadeDeClasse(nivel: Int): List<String> {
        val habilidades = mutableListOf("APARAR: Pode sacrificar escudo ou arma para absorver todo o dano de um ataque")
        when (nivel) {
            in 1 .. 2 -> habilidades.add("MAESTRIA EM ARMA: Bônus de +1 no dano com uma arma à escolha.")
            in 3 .. 9 -> habilidades.add("MAESTRIA EM ARMA: Bônus de +2 no dano com duas armas escolhidas")
            else -> habilidades.add("MAESTRIA EM ARMA: Bonus de +3 nas classes de armas ja escolhidas")
        }
        if( nivel >= 6){
            habilidades.add("ATAQUE EXTRA: Adquire um segundo ataque por rodada.")
        }
        return habilidades
    }
    override fun podeUsarArmadura(tipo: String): Boolean = true
    override fun obterBA(nivel: Int): Int { return nivel}
    override fun obterJP(nivel: Int): Int {
        return when(nivel){
            in 1..2 -> 5
            in 3..4 -> 6
            in 5..6 -> 8
            in 7..8 -> 10
            else -> 11
        }
    }
    override fun obterEquipamentoInicial(): MutableList<Equipamento> {
        return mutableListOf(
            Arma(nome = "Espada Longa", carga = 2, dano = "1d10"),
            Armadura(nome = "Cota de Malha", carga = 4, bonusCA = 4)
        )
    }
}

class Mago : Classe("Mago", 4){
    //botar especializações
    override fun obterHabilidadeDeClasse(nivel: Int): List<String> {
        return listOf(
            "MAGIAS ARCANAS: Capaz de lançar magias arcanas diariamente a partir do Grimório.",
            //definir circulos e tipos e magia e seleção de magia
            "LER MAGIAS: Capaz de decifrar inscrições mágicas.",
            "DETECTAR MAGIAS: Capaz de perceber a presença de magia."
        )
    }
    override fun podeUsarArmadura(tipo: String): Boolean = false
    override fun obterBA(nivel: Int): Int{
        return when(nivel){
            1 -> 0
            in 2 .. 4 -> 1
            in 5 .. 7 -> 2
            else -> 3
        }
    }
    override fun obterJP(nivel: Int): Int{
        return when(nivel){
            in 1..4 ->5
            in 5..9 -> 7
            else -> 10
        }
    }
    override fun obterEquipamentoInicial(): MutableList<Equipamento> {
        return mutableListOf(
            Arma(nome = "Adaga", carga = 0, dano = "1d6"),
            ItemGeral(nome = "Grimório de Iniciação", carga = 1)
        )
    }
}

class Ladrao : Classe("Ladrao", 6){
    //botar especializaçoes
    override fun obterHabilidadeDeClasse(nivel: Int): List<String> {
        val habilidades = mutableListOf<String>()
        habilidades.add("TALENTOS: Armadilha, Arrombar, Escalar, Furtividade, Punga")
        when(nivel){
            in 1 .. 2 -> {
                habilidades.add("OUVIR RUIDOS: Chance de 1-2 em 1d6 para conseguir ouvir ruidos")
                habilidades.add("TALENTO DE LADRAO: +2 pontos para cada um dos cinco talentos e 2 pontos para distribuir")
            }
            in 3 .. 5 ->{
                habilidades.add("OUVIR RUIDOS: Chance de 1-4 em 1d6 para ouvir ruidos")
                habilidades.add("TALENTO DE LADRAO: +2 pontos para evoluir talentos")
            }
            else -> {
                habilidades.add("OUVIR RUIDOS: Chance de 1-5 em 1d6 para ouvir ruidos")
                habilidades.add("TALENTO DE LADRAO: +2 pontos para evoluir")
            }
        }
        val danoMultiplicado = when {
            nivel >= 10 -> "multiplicado por 4."
            nivel >= 6 -> "multiplicado por 3."
            else -> "multiplicado por 2."
        }
        habilidades.add("ATAQUE FURTIVO: Dano $danoMultiplicado")
        return habilidades
    }
    override fun podeUsarArmadura(tipo: String): Boolean = tipo == "Leve"
    override fun obterBA(nivel: Int): Int {
        return when(nivel){
            in 1..2 -> 1
            in 3..4 -> 2
            in 5..6 -> 3
            in 7..8 -> 4
            else -> 5
        }
    }
    override fun obterJP(nivel: Int): Int{
        return when(nivel){
            in 1 .. 4 -> 5
            in 5..8 -> 8
            else -> 11
        }
    }
    override fun obterEquipamentoInicial(): MutableList<Equipamento> {
        return mutableListOf(
            Arma(nome = "Adaga", carga = 0, dano = "1d8"),
            Armadura(nome = "Armadura de Couro", carga = 1, bonusCA = 2)
        )
    }
}