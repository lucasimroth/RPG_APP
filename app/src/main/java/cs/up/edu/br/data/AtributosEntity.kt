package cs.up.edu.br.data

/**
 * Esta data class representa os atributos como eles serão
 * salvos no banco. Note que ela não tem os campos calculados
 * (modificadorForca, etc.).
 */
data class AtributosEntity(
    val forca: Int,
    val destreza: Int,
    val constituicao: Int,
    val inteligencia: Int,
    val sabedoria: Int,
    val carisma: Int
)