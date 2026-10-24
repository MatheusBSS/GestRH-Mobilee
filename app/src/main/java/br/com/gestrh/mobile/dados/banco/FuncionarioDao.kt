package br.com.gestrh.mobile.dados.banco

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.gestrh.mobile.dados.modelos.Funcionario
import kotlinx.coroutines.flow.Flow

@Dao
interface FuncionarioDao {
    @Insert
    suspend fun inserir(funcionario: Funcionario): Long

    @Query("SELECT * FROM funcionarios ORDER BY nome COLLATE NOCASE ASC, id ASC")
    fun observarTodos(): Flow<List<Funcionario>>
}
