package br.com.gestrh.mobile.apresentacao.telas

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.gestrh.mobile.R

@Composable
fun TelaCadastroFuncionario(
    aoSalvar: (String, String) -> Unit,
    aoVoltar: () -> Unit,
    salvando: Boolean,
    salvo: Boolean,
    falhou: Boolean
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var observacao by rememberSaveable { mutableStateOf("") }
    var tentouSalvar by rememberSaveable { mutableStateOf(false) }
    val erroNome = tentouSalvar && nome.isBlank()
    val editavel = !salvando && !salvo
    BackHandler { if (!salvando) aoVoltar() }
    Scaffold { espacoDasBarras ->
        Column(
            Modifier.fillMaxSize().padding(espacoDasBarras).imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.novo_funcionario), style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = nome, onValueChange = { nome = it },
                label = { Text(stringResource(R.string.nome_funcionario)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = editavel,
                isError = erroNome,
                supportingText = { if (erroNome) Text(stringResource(R.string.erro_nome_funcionario)) }
            )
            OutlinedTextField(
                value = observacao, onValueChange = { observacao = it },
                label = { Text(stringResource(R.string.observacao_demanda)) },
                modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 6, enabled = editavel
            )
            if (falhou) Text(stringResource(R.string.falha_salvamento), color = MaterialTheme.colorScheme.error)
            if (salvo) {
                Text(stringResource(R.string.funcionario_salvo), color = MaterialTheme.colorScheme.primary)
            } else {
                Button(onClick = {
                    tentouSalvar = true
                    if (nome.isNotBlank()) aoSalvar(nome, observacao)
                }, enabled = !salvando, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(if (salvando) R.string.salvando_demanda else R.string.salvar_funcionario))
                }
            }
            TextButton(onClick = aoVoltar, enabled = !salvando, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(if (salvo) R.string.voltar_funcionarios else R.string.cancelar_cadastro))
            }
        }
    }
}
