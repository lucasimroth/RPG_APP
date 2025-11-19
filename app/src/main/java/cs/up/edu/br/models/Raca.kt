package cs.up.edu.br.models

sealed class Raca (
    val nome: String,
    val movimentoBase: Int, //nao utiliado ainda
    val infravisao: Int //nao utilizado ainda
){
    abstract fun obterHabilidadesRaciais(): List<String>
}

class Humano : Raca("Humano", 9, 0){
    override fun obterHabilidadesRaciais(): List<String> {
        return listOf(
            "APRENDIZADO: Bônus de 10% sobre toda experiência (XP) recebida. ",
            "ADAPTABILIDADE: Bônus de +1 em uma única Jogada de Proteção à sua escolha. "
        )
    }
}

class Elfo : Raca("Elfo", 10, 18){
    override fun obterHabilidadesRaciais(): List<String> {
        return listOf(
            "PERCEPÇÃO NATURAL: Chance de detectar portas secretas ao passar a até 6 metros.",
            "GRACIOSOS: Bônus de +1 em qualquer teste de JPD. ",
            "ARMA RACIAL: Bônus de +1 nos danos com ataques à distância com arcos. ",
            "IMUNIDADES: Imune a sono mágico e à paralisia de Ghouls. "
        )
    }
}

class Anao : Raca("Anao", 6, 18){
    override fun obterHabilidadesRaciais(): List<String> {
        return listOf(
            "MINERADORES: Chance de detectar anomalias em construções de pedra.",
            "VIGOROSO: Bônus de +1 em qualquer teste de JPC.",
            "INIMIGOS: Ataques contra orcs, ogros e hobgoblins são considerados fáceis."
        )
    }
}

class Halfling : Raca("Halfling", 6, 0 ){
    override fun obterHabilidadesRaciais(): List<String> {
        return listOf(
            "FURTIVOS: Especialistas em se esconder, com chance de 1-2 em 1d6.",
            "DESTEMIDOS: Bônus de +1 em qualquer teste de JPS. ",
            "BONS DE MIRA: Ataques à distância com arma de arremesso são considerados fáceis. ",
            "PEQUENOS: Ataques de criaturas grandes ou maiores são considerados difíceis para acertá-lo. "
        )
    }
}