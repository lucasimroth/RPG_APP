package cs.up.edu.br.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [PersonagemEntity::class, EquipamentoEntity::class],
    version = 1,
    exportSchema = false // Por enquanto
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personagemDao(): PersonagemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rpg_database"
                )
                    .fallbackToDestructiveMigration() // Use migração correta em produção
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}