package br.com.gestrh.mobile.apresentacao.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import br.com.gestrh.mobile.apresentacao.funcionarios.EstadoFuncionarios

@Composable
fun SeletorFuncionario(
    estado: EstadoFuncionarios,
    selecionado: Long?,
    habilitado: Boolean,
    aoSelecionar: (Long?) -> Unit,
    aoTentarNovamente: () -> Unit
) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    val nome = estado.funcionarios.firstOrNull { it.id == selecionado }?.nome
    Text(stringResource(R.string.funcionario_opcional), style = MaterialTheme.typography.titleMedium)
    OutlinedButton(
        onClick = { aberto = true },
        enabled = habilitado && !estado.carregando && !estado.falhou,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (selecionado == null) stringResource(R.string.sem_funcionario)
            else nome ?: stringResource(R.string.funcionario_indisponivel))
    }
    if (selecionado != null) {
        TextButton(onClick = { aoSelecionar(null) }, enabled = habilitado) {
            Text(stringResource(R.string.remover_vinculo))
        }
    }
    when {
        estado.carregando -> Text(stringResource(R.string.carregando_funcionarios))
        estado.falhou -> {
            Text(stringResource(R.string.falha_consulta_funcionarios))
            TextButton(onClick = aoTentarNovamente, enabled = habilitado) {
                Text(stringResource(R.string.tentar_novamente))
            }
        }
        estado.funcionarios.isEmpty() -> Text(stringResource(R.string.cadastre_funcionario_depois))
    }
    if (aberto && habilitado && !estado.carregando && !estado.falhou) {
        AlertDialog(
            onDismissRequest = { aberto = false },
            title = { Text(stringResource(R.string.selecionar_funcionario)) },
            text = {
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    item {
                        TextButton(onClick = { aoSelecionar(null); aberto = false }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.sem_funcionario))
                        }
                    }
                    items(estado.funcionarios, key = { it.id }) { funcionario ->
                        TextButton(onClick = { aoSelecionar(funcionario.id); aberto = false }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(funcionario.nome)
                                if (funcionario.observacao.isNotBlank()) {
                                    Text(funcionario.observacao, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { aberto = false }) { Text(stringResource(R.string.cancelar_cadastro)) }
            }
        )
    }
}
