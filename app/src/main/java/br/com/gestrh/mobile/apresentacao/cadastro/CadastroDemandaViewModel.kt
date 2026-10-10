package br.com.gestrh.mobile.apresentacao.cadastro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CadastroDemandaViewModel(private val repositorio: RepositorioDemandas) : ViewModel() {
    var salvando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var falhou by mutableStateOf(false)
        private set

    fun iniciarCadastro() {
        if (salvando) return
        salvo = false
        falhou = false
    }

    fun salvar(titulo: String, prazo: LocalDate, observacao: String, situacao: SituacaoDemanda) {
        if (salvando || salvo) return
        salvando = true
        falhou = false
        viewModelScope.launch {
            try {
                repositorio.cadastrar(titulo, prazo, observacao, situacao)
                salvo = true
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                falhou = true
            } finally {
                salvando = false
            }
        }
    }
}
