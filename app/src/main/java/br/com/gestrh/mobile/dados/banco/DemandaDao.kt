package br.com.gestrh.mobile.dados.banco

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.gestrh.mobile.dados.modelos.Demanda
import kotlinx.coroutines.flow.Flow

@Dao
interface DemandaDao {
    @Insert
    suspend fun inserir(demanda: Demanda): Long

    @Query("SELECT * FROM demandas ORDER BY prazo ASC, id ASC")
    fun observarTodas(): Flow<List<Demanda>>

    @Query("SELECT * FROM demandas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Demanda?
}
