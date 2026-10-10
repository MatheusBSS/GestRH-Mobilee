package br.com.gestrh.mobile.apresentacao

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH
import br.com.gestrh.mobile.dados.RepositorioDemandas
import br.com.gestrh.mobile.dados.banco.BancoDeDados
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FluxoDemandasTest {
    @get:Rule
    val tela = createAndroidComposeRule<ComponentActivity>()
    private lateinit var banco: BancoDeDados
    private lateinit var repositorio: RepositorioDemandas

    @Before
    fun preparar() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        banco = Room.inMemoryDatabaseBuilder(contexto, BancoDeDados::class.java).build()
        repositorio = RepositorioDemandas(banco.demandas())
        tela.setContent { TemaGestRH { AplicativoGestRH(repositorio) } }
        aguardarTexto("Nenhuma demanda cadastrada")
    }

    @After
    fun limpar() {
        tela.runOnIdle { tela.activity.viewModelStore.clear() }
        banco.close()
    }

    private fun aguardarTexto(texto: String) {
        tela.waitUntil(5_000) {
            tela.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun cadastraEConsultaDemandaSemReabrirAplicativo() {
        tela.onNodeWithText("Nova demanda").performClick()
        tela.onNodeWithText("Título *").performTextInput("Férias de exemplo")
        tela.onNodeWithText("Observação (opcional)").performScrollTo().performTextInput("Conferir datas")
        tela.onNodeWithText("Selecionar prazo *").performScrollTo().performClick()
        onView(withId(android.R.id.button1)).perform(click())
        tela.onNodeWithText("Em andamento").performScrollTo().performClick()
        tela.onNodeWithText("Salvar demanda").performScrollTo().performClick()
        aguardarTexto("Demanda salva no celular.")
        tela.onNodeWithText("Voltar ao início").performScrollTo().performClick()
        aguardarTexto("Férias de exemplo")
        tela.onNodeWithText("Férias de exemplo").assertIsDisplayed()
        tela.onNodeWithText("Situação: Em andamento").assertIsDisplayed()
        tela.onNodeWithText("Conferir datas").assertIsDisplayed()
        tela.onNodeWithText("Nenhuma demanda cadastrada").assertDoesNotExist()
        runBlocking {
            val registros = repositorio.observarDemandas().first()
            assertEquals(1, registros.size)
            assertEquals(LocalDate.now(), registros.single().prazo)
            assertEquals(SituacaoDemanda.EM_ANDAMENTO, registros.single().situacao)
        }
        tela.onNodeWithText("Nova demanda").performClick()
        tela.onNodeWithText("Férias de exemplo").assertDoesNotExist()
        tela.onNodeWithText("Cancelar").performScrollTo().performClick()
        runBlocking { assertEquals(1, repositorio.observarDemandas().first().size) }
    }

    @Test
    fun cancelarPreenchimentoNaoCriaDemanda() {
        tela.onNodeWithText("Nova demanda").performClick()
        tela.onNodeWithText("Título *").performTextInput("Não salvar")
        tela.onNodeWithText("Cancelar").performScrollTo().performClick()
        aguardarTexto("Nenhuma demanda cadastrada")
        runBlocking { assertEquals(0, repositorio.observarDemandas().first().size) }
    }
}
