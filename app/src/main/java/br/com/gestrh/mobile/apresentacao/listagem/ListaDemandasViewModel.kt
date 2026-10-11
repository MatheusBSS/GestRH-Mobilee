package br.com.gestrh.mobile.apresentacao.listagem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.modelos.Demanda
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoListaDemandas(
    val demandas: List<Demanda> = emptyList(),
    val carregando: Boolean = true,
    val falhou: Boolean = false
)

class ListaDemandasViewModel(private val repositorio: RepositorioDemandas) : ViewModel() {
    private val estadoAtual = MutableStateFlow(EstadoListaDemandas())
    val estado = estadoAtual.asStateFlow()
    private var consulta: Job? = null

    init {
        carregar()
    }

    fun carregar() {
        consulta?.cancel()
        estadoAtual.value = EstadoListaDemandas()
        consulta = viewModelScope.launch {
            try {
                repositorio.observarDemandas().collect { demandas ->
                    estadoAtual.value = EstadoListaDemandas(demandas = demandas, carregando = false)
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                estadoAtual.value = EstadoListaDemandas(carregando = false, falhou = true)
            }
        }
    }
}
