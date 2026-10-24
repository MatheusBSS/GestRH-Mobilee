package br.com.gestrh.mobile.apresentacao

import br.com.gestrh.mobile.dados.RepositorioFuncionarios
import br.com.gestrh.mobile.apresentacao.funcionarios.FuncionariosViewModel
import br.com.gestrh.mobile.apresentacao.telas.TelaFuncionarios
import br.com.gestrh.mobile.apresentacao.telas.TelaCadastroFuncionario
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import br.com.gestrh.mobile.apresentacao.edicao.EdicaoDemandaViewModel
import br.com.gestrh.mobile.apresentacao.telas.TelaEdicaoDemanda
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
fun AplicativoGestRH(
    repositorio: RepositorioDemandas? = null,
    repositorioFuncionarios: RepositorioFuncionarios? = null
) {
    val contexto = LocalContext.current.applicationContext
    val fonteDados = remember(repositorio, contexto) {
        repositorio ?: RepositorioDemandas(BancoDeDados.obter(contexto).demandas())
    }
    val cadastro: CadastroDemandaViewModel = viewModel { CadastroDemandaViewModel(fonteDados) }
    val lista: ListaDemandasViewModel = viewModel { ListaDemandasViewModel(fonteDados) }
    val estadoLista by lista.estado.collectAsStateWithLifecycle()
    var mostrandoCadastro by rememberSaveable { mutableStateOf(false) }

    val edicao: EdicaoDemandaViewModel = viewModel { EdicaoDemandaViewModel(fonteDados) }
    var demandaEmEdicao by rememberSaveable { mutableStateOf<Long?>(null) }
    val idEdicao = demandaEmEdicao

    var mostrandoFuncionarios by rememberSaveable { mutableStateOf(false) }
    var cadastrandoFuncionario by rememberSaveable { mutableStateOf(false) }

    if (mostrandoFuncionarios) {
        val fonteFuncionarios = remember(repositorioFuncionarios, contexto) {
            repositorioFuncionarios ?: RepositorioFuncionarios(BancoDeDados.obter(contexto).funcionarios())
        }
        val funcionarios: FuncionariosViewModel = viewModel { FuncionariosViewModel(fonteFuncionarios) }
        val estadoFuncionarios by funcionarios.estado.collectAsStateWithLifecycle()
        if (cadastrandoFuncionario) {
            TelaCadastroFuncionario(
                aoSalvar = funcionarios::salvar,
                aoVoltar = { cadastrandoFuncionario = false },
                salvando = funcionarios.salvando,
                salvo = funcionarios.salvo,
                falhou = funcionarios.falhaSalvamento
            )
        } else {
            TelaFuncionarios(
                estado = estadoFuncionarios,
                aoNovoFuncionario = {
                    funcionarios.iniciarCadastro()
                    cadastrandoFuncionario = true
                },
                aoTentarNovamente = funcionarios::carregar,
                aoVoltar = { mostrandoFuncionarios = false }
            )
        }
    } else if (idEdicao != null) {
        LaunchedEffect(idEdicao) {
            if (edicao.demanda?.id != idEdicao && !edicao.carregando && !edicao.falhaConsulta) {
                edicao.carregar(idEdicao)
            }
        }
        key(idEdicao) {
            TelaEdicaoDemanda(edicao, aoVoltar = { demandaEmEdicao = null },
                aoTentarNovamente = { edicao.carregar(idEdicao) })
        }
    } else if (mostrandoCadastro) {
        TelaCadastroDemanda(
            aoSalvar = cadastro::salvar,
            aoVoltar = { mostrandoCadastro = false },
            salvando = cadastro.salvando,
            salvo = cadastro.salvo,
            erroAoSalvar = if (cadastro.falhou) stringResource(R.string.falha_salvamento) else null
        )
    } else {
        TelaInicial(estado = estadoLista, aoTentarNovamente = lista::carregar,
            aoFuncionarios = { mostrandoFuncionarios = true },
            aoEditarDemanda = { id ->
                edicao.carregar(id)
                demandaEmEdicao = id
            }, aoNovaDemanda = {
            cadastro.iniciarCadastro()
            mostrandoCadastro = true
        })
    }
}
