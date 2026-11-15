package br.com.gestrh.mobile.apresentacao.telas

import br.com.gestrh.mobile.apresentacao.funcionarios.EstadoFuncionarios
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.testTag
import br.com.gestrh.mobile.apresentacao.listagem.FiltroDemandas
import br.com.gestrh.mobile.apresentacao.listagem.ResumoDemandas
import br.com.gestrh.mobile.apresentacao.listagem.organizarDemandas
import br.com.gestrh.mobile.apresentacao.listagem.resumirDemandas
import br.com.gestrh.mobile.apresentacao.listagem.estaVencida
import java.time.LocalDate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.listagem.EstadoListaDemandas
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH
import br.com.gestrh.mobile.dados.modelos.Demanda
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TelaInicial(
    modifier: Modifier = Modifier,
    estado: EstadoListaDemandas = EstadoListaDemandas(carregando = false),
    aoNovaDemanda: () -> Unit = {},
    aoTentarNovamente: () -> Unit = {},
    aoEditarDemanda: (Long) -> Unit = {},
    aoFuncionarios: () -> Unit = {},
    funcionarios: EstadoFuncionarios = EstadoFuncionarios(carregando = false),
    busca: String = "",
    filtro: FiltroDemandas = FiltroDemandas.TODAS,
    hoje: LocalDate = LocalDate.now(),
    aoBuscar: (String) -> Unit = {},
    aoFiltrar: (FiltroDemandas) -> Unit = {}
) {
    val foco = LocalFocusManager.current
    val visiveis = remember(estado.demandas, filtro, busca, hoje) {
        organizarDemandas(estado.demandas, filtro, busca, hoje)
    }
    val resumo = remember(estado.demandas, hoje) { resumirDemandas(estado.demandas, hoje) }
    Scaffold(modifier = modifier) { espacoDasBarras ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(espacoDasBarras),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.home_subtitle), style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Button(onClick = aoNovaDemanda, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.nova_demanda))
                }
            }
            item {
                androidx.compose.material3.OutlinedButton(onClick = aoFuncionarios, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.funcionarios))
                }
            }
            if (!estado.carregando && !estado.falhou) {
                item { ResumoInicial(resumo) }
                item {
                    OutlinedTextField(
                        value = busca, onValueChange = aoBuscar,
                        label = { Text(stringResource(R.string.buscar_demandas)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("busca_demandas"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
                        trailingIcon = if (busca.isNotEmpty()) {
                            { TextButton(onClick = { aoBuscar("") }) { Text(stringResource(R.string.limpar_busca)) } }
                        } else null
                    )
                }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FiltroDemandas.entries.forEach { opcao ->
                            FilterChip(
                                selected = filtro == opcao,
                                onClick = { aoFiltrar(opcao); foco.clearFocus() },
                                label = { Text(stringResource(rotuloFiltro(opcao))) },
                                modifier = Modifier.testTag("filtro_${opcao.name}")
                            )
                        }
                    }
                    Text(stringResource(R.string.ordem_demandas), style = MaterialTheme.typography.bodySmall)
                }
            }
            item {
                Text(stringResource(R.string.demands_title), style = MaterialTheme.typography.titleLarge)
            }
            when {
                estado.carregando -> item {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.carregando_demandas))
                }
                estado.falhou -> item {
                    Text(stringResource(R.string.falha_consulta), color = MaterialTheme.colorScheme.error)
                    Button(onClick = aoTentarNovamente) {
                        Text(stringResource(R.string.tentar_novamente))
                    }
                }
                estado.demandas.isEmpty() -> item {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.empty_title), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.empty_description))
                        }
                    }
                }
                visiveis.isEmpty() -> item {
                    Text(stringResource(R.string.nenhum_resultado), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.rever_busca_filtros))
                    TextButton(onClick = { aoBuscar(""); aoFiltrar(FiltroDemandas.TODAS); foco.clearFocus() }) {
                        Text(stringResource(R.string.limpar_busca_filtros))
                    }
                }
                else -> items(visiveis, key = { it.id }) { demanda ->
                    CartaoDemanda(demanda, hoje = hoje, nomeFuncionario = funcionarios.funcionarios.firstOrNull { it.id == demanda.funcionarioId }?.nome, aoEditar = { aoEditarDemanda(demanda.id) })
                }
            }
        }
    }
}

@Composable
private fun CartaoDemanda(demanda: Demanda, hoje: LocalDate, nomeFuncionario: String?, aoEditar: () -> Unit) {
    val situacao = when (demanda.situacao) {
        SituacaoDemanda.PENDENTE -> R.string.situacao_pendente
        SituacaoDemanda.EM_ANDAMENTO -> R.string.situacao_em_andamento
        SituacaoDemanda.CONCLUIDA -> R.string.situacao_concluida
    }
    OutlinedCard(onClick = aoEditar, modifier = Modifier.fillMaxWidth().testTag("demanda_${demanda.id}")) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(demanda.titulo, style = MaterialTheme.typography.titleMedium)
            if (demanda.estaVencida(hoje)) {
                Text(stringResource(R.string.demanda_vencida), color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge)
            }
            Text(stringResource(R.string.prazo_selecionado,
                demanda.prazo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
            Text(stringResource(R.string.situacao_lista, stringResource(situacao)))
            if (demanda.funcionarioId != null) {
                Text(stringResource(R.string.funcionario_lista,
                    nomeFuncionario ?: stringResource(R.string.funcionario_indisponivel)))
            }
            if (demanda.observacao.isNotBlank()) {
                Text(demanda.observacao, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, locale = "pt-rBR")
@Composable
private fun PreviaTelaInicial() {
    TemaGestRH { TelaInicial() }
}


private fun rotuloFiltro(filtro: FiltroDemandas): Int = when (filtro) {
    FiltroDemandas.TODAS -> R.string.filtro_todas
    FiltroDemandas.PENDENTES -> R.string.filtro_pendentes
    FiltroDemandas.EM_ANDAMENTO -> R.string.situacao_em_andamento
    FiltroDemandas.CONCLUIDAS -> R.string.filtro_concluidas
    FiltroDemandas.VENCIDAS -> R.string.filtro_vencidas
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResumoInicial(resumo: ResumoDemandas) {
    OutlinedCard(modifier = Modifier.fillMaxWidth().testTag("resumo_demandas")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.resumo_demandas), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.resumo_pendentes, resumo.pendentes))
                Text(stringResource(R.string.resumo_andamento, resumo.emAndamento))
                Text(stringResource(R.string.resumo_concluidas, resumo.concluidas))
                Text(stringResource(R.string.resumo_vencidas, resumo.vencidas), color = MaterialTheme.colorScheme.error)
            }
            Text(stringResource(R.string.explicacao_resumo), style = MaterialTheme.typography.bodySmall)
        }
    }
}
