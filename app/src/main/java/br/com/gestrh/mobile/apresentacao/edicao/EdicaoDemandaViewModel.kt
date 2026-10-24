package br.com.gestrh.mobile.apresentacao.edicao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.modelos.Demanda
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EdicaoDemandaViewModel(private val repositorio: RepositorioDemandas) : ViewModel() {
    var demanda by mutableStateOf<Demanda?>(null)
        private set
    var carregando by mutableStateOf(false)
        private set
    var falhaConsulta by mutableStateOf(false)
        private set
    var salvando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var falhaSalvamento by mutableStateOf(false)
        private set
    private var consulta: Job? = null

    fun carregar(id: Long) {
        consulta?.cancel()
        demanda = null
        carregando = true
        falhaConsulta = false
        salvo = false
        falhaSalvamento = false
        consulta = viewModelScope.launch {
            try {
                demanda = repositorio.buscarDemanda(id)
                falhaConsulta = demanda == null
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                falhaConsulta = true
            }
            carregando = false
        }
    }

    fun salvar(titulo: String, prazo: LocalDate, observacao: String) {
        val atual = demanda ?: return
        if (salvando || salvo) return
        salvando = true
        falhaSalvamento = false
        viewModelScope.launch {
            try {
                repositorio.editar(atual.id, titulo, prazo, observacao)
                salvo = true
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                falhaSalvamento = true
            } finally {
                salvando = false
            }
        }
    }
}
