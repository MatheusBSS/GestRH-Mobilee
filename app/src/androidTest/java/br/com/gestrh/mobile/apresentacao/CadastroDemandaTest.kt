package br.com.gestrh.mobile.apresentacao

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.com.gestrh.mobile.apresentacao.telas.TelaCadastroDemanda
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CadastroDemandaTest {
    @get:Rule
    val tela = createComposeRule()

    @Test
    fun impedeEnvioSemTituloEPrazo() {
        var envios = 0
        tela.setContent {
            TemaGestRH {
                TelaCadastroDemanda(aoSalvar = { _, _, _, _ -> envios++ }, aoVoltar = {})
            }
        }
        tela.onNodeWithText("Salvar demanda").performScrollTo().performClick()
        tela.onNodeWithText("Informe o título da demanda.").performScrollTo().assertIsDisplayed()
        tela.onNodeWithText("Selecione o prazo da demanda.").performScrollTo().assertIsDisplayed()
        tela.runOnIdle { assertEquals(0, envios) }
    }

    @Test
    fun cancelarVoltaSemEnviar() {
        var envios = 0
        var retornos = 0
        tela.setContent {
            TemaGestRH {
                TelaCadastroDemanda(aoSalvar = { _, _, _, _ -> envios++ }, aoVoltar = { retornos++ })
            }
        }
        tela.onNodeWithText("Cancelar").performScrollTo().performClick()
        tela.runOnIdle {
            assertEquals(0, envios)
            assertEquals(1, retornos)
        }
    }
}
