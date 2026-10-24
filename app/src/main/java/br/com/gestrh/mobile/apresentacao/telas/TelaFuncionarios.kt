package br.com.gestrh.mobile.apresentacao.telas

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.funcionarios.EstadoFuncionarios

@Composable
fun TelaFuncionarios(
    estado: EstadoFuncionarios,
    aoNovoFuncionario: () -> Unit,
    aoTentarNovamente: () -> Unit,
    aoVoltar: () -> Unit
) {
    BackHandler(onBack = aoVoltar)
    Scaffold { espacoDasBarras ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(espacoDasBarras),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(stringResource(R.string.funcionarios), style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = aoVoltar) { Text(stringResource(R.string.voltar_inicio)) }
                Button(onClick = aoNovoFuncionario, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.novo_funcionario))
                }
            }
            when {
                estado.carregando -> item {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.carregando_funcionarios))
                }
                estado.falhou -> item {
                    Text(stringResource(R.string.falha_consulta_funcionarios), color = MaterialTheme.colorScheme.error)
                    Button(onClick = aoTentarNovamente) { Text(stringResource(R.string.tentar_novamente)) }
                }
                estado.funcionarios.isEmpty() -> item {
                    Text(stringResource(R.string.nenhum_funcionario))
                }
                else -> items(estado.funcionarios, key = { it.id }) { funcionario ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(funcionario.nome, style = MaterialTheme.typography.titleMedium)
                            if (funcionario.observacao.isNotBlank()) Text(funcionario.observacao)
                        }
                    }
                }
            }
        }
    }
}
