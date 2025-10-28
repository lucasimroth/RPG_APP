package cs.up.edu.br

import android.app.Application
import cs.up.edu.br.data.PersonagemRepository
import cs.up.edu.br.data.AppDatabase

/**
 * Uma classe Application customizada.
 * Ela é o "dono" do nosso banco de dados e repositório,
 * garantindo que eles existam durante todo o ciclo de vida do app.
 */
class MainApplication : Application() {

    // Usamos 'lazy' para que o banco e o repo só sejam criados
    // quando forem realmente necessários pela primeira vez.
    private val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy { PersonagemRepository(database.personagemDao()) }
}