package cs.up.edu.br

import android.app.Application
import cs.up.edu.br.data.PersonagemRepository
import cs.up.edu.br.data.AppDatabase

/**
 * Uma classe Application customizada.
 * é o "dono" do banco de dados e repositório,
 * garante que eles existam durante todo o ciclo de vida do app.
 */
class MainApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy { PersonagemRepository(database.personagemDao()) }
}