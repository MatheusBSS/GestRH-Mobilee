package br.com.gestrh.mobile.apresentacao.telas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.gestrh.mobile.R
import br.com.gestrh.mobile.apresentacao.edicao.EdicaoDemandaViewModel

@Composable
fun TelaEdicaoDemanda(
    edicao: EdicaoDemandaViewModel,
    aoVoltar: () -> Unit,
    aoTentarNovamente: () -> Unit
) {
    val demanda = edicao.demanda
    if (demanda != null) {
        TelaCadastroDemanda(
            demandaInicial = demanda,
            aoSalvar = { titulo, prazo, observacao, _ -> edicao.salvar(titulo, prazo, observacao) },
            aoVoltar = aoVoltar,
            salvando = edicao.salvando,
            salvo = edicao.salvo,
            erroAoSalvar = if (edicao.falhaSalvamento) stringResource(R.string.falha_salvamento) else null
        )
    } else {
        BackHandler(onBack = aoVoltar)
        Scaffold { espacoDasBarras ->
            Column(Modifier.fillMaxSize().padding(espacoDasBarras).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.editar_demanda))
                if (edicao.falhaConsulta) {
                    Text(stringResource(R.string.falha_carregar_demanda))
                    Button(onClick = aoTentarNovamente) { Text(stringResource(R.string.tentar_novamente)) }
                } else {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.carregando_demanda))
                }
                TextButton(onClick = aoVoltar) { Text(stringResource(R.string.voltar_inicio)) }
            }
        }
    }
}
