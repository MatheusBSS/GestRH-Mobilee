package br.com.gestrh.mobile.apresentacao.telas

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

@Composable
fun TelaInicial(
    modifier: Modifier = Modifier,
    estado: EstadoListaDemandas = EstadoListaDemandas(carregando = false),
    aoNovaDemanda: () -> Unit = {},
    aoTentarNovamente: () -> Unit = {}
) {
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
                else -> items(estado.demandas, key = { it.id }) { demanda ->
                    CartaoDemanda(demanda)
                }
            }
        }
    }
}

@Composable
private fun CartaoDemanda(demanda: Demanda) {
    val situacao = when (demanda.situacao) {
        SituacaoDemanda.PENDENTE -> R.string.situacao_pendente
        SituacaoDemanda.EM_ANDAMENTO -> R.string.situacao_em_andamento
        SituacaoDemanda.CONCLUIDA -> R.string.situacao_concluida
    }
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(demanda.titulo, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.prazo_selecionado,
                demanda.prazo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
            Text(stringResource(R.string.situacao_lista, stringResource(situacao)))
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
