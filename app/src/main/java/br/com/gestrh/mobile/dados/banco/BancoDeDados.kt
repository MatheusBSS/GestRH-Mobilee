package br.com.gestrh.mobile.dados.banco

import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.gestrh.mobile.dados.modelos.Funcionario
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.gestrh.mobile.dados.modelos.Demanda

@Database(entities = [Demanda::class, Funcionario::class], version = 2, exportSchema = true)
@TypeConverters(ConversoresBanco::class)
abstract class BancoDeDados : RoomDatabase() {
    abstract fun demandas(): DemandaDao
    abstract fun funcionarios(): FuncionarioDao

    companion object {
        val MIGRACAO_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS funcionarios (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nome TEXT NOT NULL, observacao TEXT NOT NULL)")
            }
        }

        @Volatile
        private var instancia: BancoDeDados? = null

        fun obter(contexto: Context): BancoDeDados = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                contexto.applicationContext,
                BancoDeDados::class.java,
                "gestrh.db"
            ).addMigrations(MIGRACAO_1_2).build().also { instancia = it }
        }
    }
}
