package br.com.gestrh.mobile.dados.banco

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.gestrh.mobile.dados.modelos.Demanda

@Database(entities = [Demanda::class], version = 1, exportSchema = true)
@TypeConverters(ConversoresBanco::class)
abstract class BancoDeDados : RoomDatabase() {
    abstract fun demandas(): DemandaDao

    companion object {
        @Volatile
        private var instancia: BancoDeDados? = null

        fun obter(contexto: Context): BancoDeDados = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                contexto.applicationContext,
                BancoDeDados::class.java,
                "gestrh.db"
            ).build().also { instancia = it }
        }
    }
}
