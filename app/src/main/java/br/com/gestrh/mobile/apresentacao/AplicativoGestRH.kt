package br.com.gestrh.mobile.apresentacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.cadastro.CadastroDemandaViewModel
import br.com.gestrh.mobile.apresentacao.telas.TelaCadastroDemanda
import br.com.gestrh.mobile.apresentacao.telas.TelaInicial
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.banco.BancoDeDados

@Composable
fun AplicativoGestRH() {
    val contexto = LocalContext.current.applicationContext
    val cadastro: CadastroDemandaViewModel = viewModel {
        CadastroDemandaViewModel(RepositorioDemandas(BancoDeDados.obter(contexto).demandas()))
    }
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
        TelaInicial(aoNovaDemanda = {
            cadastro.iniciarCadastro()
            mostrandoCadastro = true
        })
    }
}
