package br.com.gestrh.mobile.apresentacao.telas

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TelaCadastroDemanda(
    aoSalvar: (String, LocalDate, String, SituacaoDemanda) -> Unit,
    aoVoltar: () -> Unit,
    salvando: Boolean = false,
    salvo: Boolean = false,
    erroAoSalvar: String? = null
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var observacao by rememberSaveable { mutableStateOf("") }
    var prazoEmDias by rememberSaveable { mutableStateOf<Long?>(null) }
    var situacaoEscolhida by rememberSaveable { mutableStateOf(SituacaoDemanda.PENDENTE.name) }
    var tentouSalvar by rememberSaveable { mutableStateOf(false) }
    val contexto = LocalContext.current
    val prazo = prazoEmDias?.let(LocalDate::ofEpochDay)
    val editavel = !salvando && !salvo
    val erroTitulo = tentouSalvar && titulo.isBlank()
    val erroPrazo = tentouSalvar && prazo == null

    BackHandler { if (!salvando) aoVoltar() }

    Scaffold { espacoDasBarras ->
        Column(
            modifier = Modifier.fillMaxSize().padding(espacoDasBarras)
                .imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.nova_demanda), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.instrucao_cadastro))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text(stringResource(R.string.titulo_demanda)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = editavel,
                isError = erroTitulo,
                supportingText = { if (erroTitulo) Text(stringResource(R.string.erro_titulo)) }
            )
            OutlinedTextField(
                value = observacao,
                onValueChange = { observacao = it },
                label = { Text(stringResource(R.string.observacao_demanda)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = editavel
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = editavel,
                onClick = {
                    val dataInicial = prazo ?: LocalDate.now()
                    DatePickerDialog(
                        contexto,
                        { _, ano, mes, dia -> prazoEmDias = LocalDate.of(ano, mes + 1, dia).toEpochDay() },
                        dataInicial.year, dataInicial.monthValue - 1, dataInicial.dayOfMonth
                    ).show()
                }
            ) {
                Text(
                    prazo?.let { stringResource(R.string.prazo_selecionado, it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }
                        ?: stringResource(R.string.selecionar_prazo)
                )
            }
            if (erroPrazo) Text(stringResource(R.string.erro_prazo), color = MaterialTheme.colorScheme.error)
            Text(stringResource(R.string.situacao_demanda), style = MaterialTheme.typography.titleMedium)
            SituacaoDemanda.entries.forEach { situacao ->
                val rotulo = when (situacao) {
                    SituacaoDemanda.PENDENTE -> R.string.situacao_pendente
                    SituacaoDemanda.EM_ANDAMENTO -> R.string.situacao_em_andamento
                    SituacaoDemanda.CONCLUIDA -> R.string.situacao_concluida
                }
                androidx.compose.material3.FilterChip(
                    selected = situacaoEscolhida == situacao.name,
                    onClick = { situacaoEscolhida = situacao.name },
                    label = { Text(stringResource(rotulo)) },
                    enabled = editavel
                )
            }
            if (erroAoSalvar != null) Text(erroAoSalvar, color = MaterialTheme.colorScheme.error)
            if (salvo) {
                Text(stringResource(R.string.demanda_salva), color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando,
                    onClick = {
                        tentouSalvar = true
                        if (titulo.isNotBlank() && prazo != null) {
                            aoSalvar(titulo, prazo, observacao, SituacaoDemanda.valueOf(situacaoEscolhida))
                        }
                    }
                ) {
                    Text(stringResource(if (salvando) R.string.salvando_demanda else R.string.salvar_demanda))
                }
            }
            TextButton(onClick = aoVoltar, enabled = !salvando, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(if (salvo) R.string.voltar_inicio else R.string.cancelar_cadastro))
            }
        }
    }
}

@Preview(showBackground = true, locale = "pt-rBR")
@Composable
private fun PreviaCadastroDemanda() {
    TemaGestRH { TelaCadastroDemanda(aoSalvar = { _, _, _, _ -> }, aoVoltar = {}) }
}
