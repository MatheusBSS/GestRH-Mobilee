package br.com.gestrh.mobile.apresentacao

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.RepositorioFuncionarios
import br.com.gestrh.mobile.dados.banco.BancoDeDados
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FluxoFuncionariosTest {
    @get:Rule
    val tela = createAndroidComposeRule<ComponentActivity>()
    private lateinit var banco: BancoDeDados
    private lateinit var funcionarios: RepositorioFuncionarios

    @Before
    fun preparar() {
        banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(), BancoDeDados::class.java
        ).build()
        funcionarios = RepositorioFuncionarios(banco.funcionarios())
        tela.setContent {
            TemaGestRH { AplicativoGestRH(RepositorioDemandas(banco.demandas()), funcionarios) }
        }
        tela.onNodeWithText("Funcionários").performScrollTo().performClick()
        aguardarTexto("Nenhum funcionário cadastrado")
    }

    @After
    fun limpar() {
        tela.runOnIdle { tela.activity.viewModelStore.clear() }
        banco.close()
    }

    private fun aguardarTexto(texto: String) {
        tela.waitUntil(5_000) { tela.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test
    fun validaNomeCadastraEConsultaFuncionario() {
        tela.onNodeWithText("Novo funcionário").performClick()
        tela.onNodeWithText("Salvar funcionário").performScrollTo().performClick()
        aguardarTexto("Informe o nome do funcionário.")
        runBlocking { assertEquals(0, funcionarios.observarFuncionarios().first().size) }
        tela.onNodeWithText("Nome *").performScrollTo().performTextInput("Ana de exemplo")
        tela.onNodeWithText("Observação (opcional)").performScrollTo().performTextInput("Administrativo")
        tela.onNodeWithText("Salvar funcionário").performScrollTo().performClick()
        aguardarTexto("Funcionário salvo no celular.")
        tela.onNodeWithText("Voltar aos funcionários").performScrollTo().performClick()
        aguardarTexto("Ana de exemplo")
        runBlocking {
            val registro = funcionarios.observarFuncionarios().first().single()
            assertEquals("Ana de exemplo", registro.nome)
            assertEquals("Administrativo", registro.observacao)
        }
        tela.onNodeWithText("Novo funcionário").performScrollTo().performClick()
        tela.onNodeWithText("Ana de exemplo").assertDoesNotExist()
        tela.onNodeWithText("Cancelar").performScrollTo().performClick()
        tela.onNodeWithText("Voltar ao início").performScrollTo().performClick()
        aguardarTexto("Nova demanda")
    }

    @Test
    fun cancelarNaoCadastraFuncionario() {
        tela.onNodeWithText("Novo funcionário").performClick()
        tela.onNodeWithText("Nome *").performTextInput("Não salvar")
        tela.onNodeWithText("Cancelar").performScrollTo().performClick()
        aguardarTexto("Nenhum funcionário cadastrado")
        runBlocking { assertEquals(0, funcionarios.observarFuncionarios().first().size) }
    }
}
