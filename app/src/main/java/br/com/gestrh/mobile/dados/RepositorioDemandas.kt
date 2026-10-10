package br.com.gestrh.mobile.dados

import br.com.gestrh.mobile.dados.banco.DemandaDao
import br.com.gestrh.mobile.dados.modelos.Demanda
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class RepositorioDemandas(private val consultas: DemandaDao) {
    fun observarDemandas(): Flow<List<Demanda>> = consultas.observarTodas()

    suspend fun buscarDemanda(id: Long): Demanda? = consultas.buscarPorId(id)

    suspend fun cadastrar(
        titulo: String,
        prazo: LocalDate,
        observacao: String = "",
        situacao: SituacaoDemanda = SituacaoDemanda.PENDENTE
    ): Long {
        val tituloAjustado = titulo.trim()
        require(tituloAjustado.isNotEmpty()) { "Informe o título da demanda." }
        return consultas.inserir(
            Demanda(
                titulo = tituloAjustado,
                prazo = prazo,
                observacao = observacao.trim(),
                situacao = situacao
            )
        )
    }
}
