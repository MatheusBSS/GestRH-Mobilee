package br.com.gestrh.mobile.apresentacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.gestrh.mobile.apresentacao.listagem.ListaDemandasViewModel
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.cadastro.CadastroDemandaViewModel
import br.com.gestrh.mobile.apresentacao.telas.TelaCadastroDemanda
import br.com.gestrh.mobile.apresentacao.telas.TelaInicial
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.banco.BancoDeDados

@Composable
fun AplicativoGestRH(repositorio: RepositorioDemandas? = null) {
    val contexto = LocalContext.current.applicationContext
    val fonteDados = remember(repositorio, contexto) {
        repositorio ?: RepositorioDemandas(BancoDeDados.obter(contexto).demandas())
    }
    val cadastro: CadastroDemandaViewModel = viewModel { CadastroDemandaViewModel(fonteDados) }
    val lista: ListaDemandasViewModel = viewModel { ListaDemandasViewModel(fonteDados) }
    val estadoLista by lista.estado.collectAsStateWithLifecycle()
    var mostrandoCadastro by rememberSaveable { mutableStateOf(false) }

    if (mostrandoCadastro) {
        TelaCadastroDemanda(
            aoSalvar = cadastro::salvar,
            aoVoltar = { mostrandoCadastro = false },
            salvando = cadastro.salvando,
            salvo = cadastro.salvo,
            erroAoSalvar = if (cadastro.falhou) stringResource(R.string.falha_salvamento) else null
        )
    } else {
        TelaInicial(estado = estadoLista, aoTentarNovamente = lista::carregar, aoNovaDemanda = {
            cadastro.iniciarCadastro()
            mostrandoCadastro = true
        })
    }
}
