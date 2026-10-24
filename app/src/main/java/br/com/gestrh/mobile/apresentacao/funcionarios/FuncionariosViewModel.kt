package br.com.gestrh.mobile.apresentacao.funcionarios

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.gestrh.mobile.dados.RepositorioFuncionarios
import br.com.gestrh.mobile.dados.modelos.Funcionario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoFuncionarios(
    val funcionarios: List<Funcionario> = emptyList(),
    val carregando: Boolean = true,
    val falhou: Boolean = false
)

class FuncionariosViewModel(private val repositorio: RepositorioFuncionarios) : ViewModel() {
    private val estadoAtual = MutableStateFlow(EstadoFuncionarios())
    val estado = estadoAtual.asStateFlow()
    private var consulta: Job? = null
    var salvando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var falhaSalvamento by mutableStateOf(false)
        private set

    init {
        carregar()
    }

    fun carregar() {
        consulta?.cancel()
        estadoAtual.value = EstadoFuncionarios()
        consulta = viewModelScope.launch {
            try {
                repositorio.observarFuncionarios().collect {
                    estadoAtual.value = EstadoFuncionarios(funcionarios = it, carregando = false)
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                estadoAtual.value = EstadoFuncionarios(carregando = false, falhou = true)
            }
        }
    }

    fun iniciarCadastro() {
        if (salvando) return
        salvo = false
        falhaSalvamento = false
    }

    fun salvar(nome: String, observacao: String) {
        if (salvando || salvo) return
        salvando = true
        falhaSalvamento = false
        viewModelScope.launch {
            try {
                repositorio.cadastrar(nome, observacao)
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
