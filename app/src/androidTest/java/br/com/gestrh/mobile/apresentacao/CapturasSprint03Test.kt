package br.com.gestrh.mobile.apresentacao

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.platform.app.InstrumentationRegistry
import br.com.gestrh.mobile.AtividadePrincipal
import br.com.gestrh.mobile.dados.banco.BancoDeDados
import br.com.gestrh.mobile.dados.modelos.Demanda
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CapturasSprint03Test {
    @get:Rule
    val tela = createAndroidComposeRule<AtividadePrincipal>()

    private fun aguardar(texto: String) {
        tela.waitUntil(15_000) {
            tela.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun capturar(nome: String) {
        tela.waitForIdle()
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val pasta = File(contexto.getExternalFilesDir(null), "capturas-sprint03").apply { mkdirs() }
        val imagem = requireNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
        File(pasta, "$nome.png").outputStream().use { imagem.compress(Bitmap.CompressFormat.PNG, 100, it) }
        imagem.recycle()
    }

    @Test
    fun registraTelasReaisDaSprint03() {
        val banco = BancoDeDados.obter(tela.activity)
        val hoje = LocalDate.now()
        val id = runBlocking {
            withContext(Dispatchers.IO) { banco.clearAllTables() }
            val primeiro = banco.demandas().inserir(Demanda(titulo = "Férias de Ana - exemplo", prazo = hoje.minusDays(2), observacao = "Conferir solicitação fictícia de férias."))
            banco.demandas().inserir(Demanda(titulo = "Conferência da folha - exemplo", prazo = hoje.minusDays(1), situacao = SituacaoDemanda.EM_ANDAMENTO))
            banco.demandas().inserir(Demanda(titulo = "Décimo terceiro - exemplo", prazo = hoje.plusDays(3)))
            banco.demandas().inserir(Demanda(titulo = "Descontos revisados - exemplo", prazo = hoje.minusDays(3), situacao = SituacaoDemanda.CONCLUIDA))
            primeiro
        }
        aguardar("Pendentes: 2")
        tela.onNodeWithTag("resumo_demandas").performScrollTo()
        tela.onNodeWithText("Vencidas: 2").assertExists()
        capturar("01-resumo-geral")

        tela.onNodeWithTag("filtro_VENCIDAS").performScrollTo().performClick()
        tela.onNodeWithTag("demanda_$id").performScrollTo()
        tela.onNodeWithText("Férias de Ana - exemplo").assertIsDisplayed()
        capturar("02-demandas-vencidas")

        tela.onNodeWithTag("busca_demandas").performScrollTo().performTextInput("FERIAS")
        closeSoftKeyboard()
        tela.onNodeWithTag("demanda_$id").performScrollTo()
        tela.onNodeWithText("Conferência da folha - exemplo").assertDoesNotExist()
        capturar("03-busca-com-filtro")

        tela.onNodeWithTag("demanda_$id").performClick()
        aguardar("Salvar alterações")
        tela.onNodeWithText("Concluída").performScrollTo().performClick()
        capturar("04-alteracao-de-situacao")
        tela.onNodeWithText("Salvar alterações").performScrollTo().performClick()
        aguardar("Alterações salvas no celular.")
        tela.onNodeWithText("Voltar ao início").performScrollTo().performClick()
        aguardar("Nenhuma demanda encontrada")
        tela.onNodeWithText("Nenhuma demanda encontrada").performScrollTo().assertIsDisplayed()
        capturar("05-concluida-sai-das-vencidas")
        runBlocking { assertEquals(SituacaoDemanda.CONCLUIDA, banco.demandas().buscarPorId(id)?.situacao) }

        tela.onNodeWithTag("filtro_CONCLUIDAS").performScrollTo().performClick()
        tela.onNodeWithTag("demanda_$id").performScrollTo()
        tela.onNodeWithText("Situação: Concluída").assertIsDisplayed()
        capturar("06-consulta-da-conclusao")
        tela.activityRule.scenario.recreate()
        aguardar("Férias de Ana - exemplo")
        tela.onNodeWithTag("demanda_$id").performScrollTo()
        tela.onNodeWithText("Situação: Concluída").assertIsDisplayed()
        capturar("07-apos-recriar-atividade")
    }
}
